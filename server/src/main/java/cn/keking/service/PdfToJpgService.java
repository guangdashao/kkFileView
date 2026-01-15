package cn.keking.service;

import cn.keking.config.ConfigConstants;
import cn.keking.model.FileAttribute;
import cn.keking.service.cache.NotResourceCache;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.poi.EncryptedDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PDF转JPG服务 - 高性能优化版本
 */
@Component
public class PdfToJpgService {
    private final FileHandlerService fileHandlerService;

    // 使用线程池替代虚拟线程，便于控制并发数
    private ExecutorService threadPoolExecutor;
    private static final Logger logger = LoggerFactory.getLogger(PdfToJpgService.class);
    private static final String PDF_PASSWORD_MSG = "password";
    private static final String PDF2JPG_IMAGE_FORMAT = ".jpg";
    private static final int BATCH_SIZE = 20;
    private static final int PARALLEL_BATCH_THRESHOLD = 100;

    // 性能监控
    private final AtomicInteger activeTaskCount = new AtomicInteger(0);
    private final AtomicInteger totalCompletedTasks = new AtomicInteger(0);

    public PdfToJpgService(FileHandlerService fileHandlerService) {
        this.fileHandlerService = fileHandlerService;
    }

    @PostConstruct
    public void init() {
        // 使用固定大小的线程池，便于控制并发数
        int maxThreads = ConfigConstants.getPdfMaxThreads();
        this.threadPoolExecutor = new ThreadPoolExecutor(
                maxThreads, // 核心线程数
                maxThreads, // 最大线程数
                60L, TimeUnit.SECONDS, // 空闲线程存活时间
                new LinkedBlockingQueue<>(100), // 任务队列
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：由调用线程执行
        );

        logger.info("PDF转换线程池初始化完成，最大线程数: {}", maxThreads);
    }

    @PreDestroy
    public void shutdown() {
        if (threadPoolExecutor != null && !threadPoolExecutor.isShutdown()) {
            threadPoolExecutor.shutdown();
            try {
                if (!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    threadPoolExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPoolExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            logger.info("PDF转换服务已关闭");
        }
    }

    /**
     * PDF转JPG - 高性能主方法
     */
    public List<String> pdf2jpg(String fileNameFilePath, String pdfFilePath,
                                String pdfName, FileAttribute fileAttribute) throws Exception {
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();
        boolean usePasswordCache = fileAttribute.getUsePasswordCache();
        String filePassword = fileAttribute.getFilePassword();

        // 检查缓存
        if (!forceUpdatedCache) {
            List<String> cacheResult = fileHandlerService.loadPdf2jpgCache(pdfFilePath);
            if (!CollectionUtils.isEmpty(cacheResult)) {
                return cacheResult;
            }
        }

        // 验证文件存在
        File pdfFile = new File(fileNameFilePath);
        if (!pdfFile.exists()) {
            logger.error("PDF文件不存在: {}", fileNameFilePath);
            return null;
        }

        // 创建输出目录
        int index = pdfFilePath.lastIndexOf(".");
        String folder = pdfFilePath.substring(0, index);
        File path = new File(folder);
        if (!path.exists() && !path.mkdirs()) {
            logger.error("创建转换文件目录失败: {}", folder);
            throw new IOException("无法创建输出目录");
        }

        // 加载PDF文档获取页数
        int pageCount;
        try (PDDocument tempDoc = Loader.loadPDF(pdfFile, filePassword)) {
            pageCount = tempDoc.getNumberOfPages();
        } catch (IOException e) {
            handlePdfLoadException(e, pdfFilePath);
            throw new Exception("PDF文件加载失败", e);
        }

        // 检查线程池负载
        checkThreadPoolLoad();

        // 根据页数选择最佳转换策略
        List<String> imageUrls;
        long startTime = System.currentTimeMillis();

        if (pageCount <= PARALLEL_BATCH_THRESHOLD) {
            imageUrls = convertOptimizedParallel(pdfFile, filePassword, pdfFilePath, folder, pageCount);
        } else {
            imageUrls = convertHighPerformance(pdfFile, filePassword, pdfFilePath, folder, pageCount);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        // 缓存结果
        if (usePasswordCache || ObjectUtils.isEmpty(filePassword)) {
            fileHandlerService.addPdf2jpgCache(pdfFilePath, pageCount);
        }

        // 性能统计
        logger.info("PDF转换完成: 总页数={}, 耗时={}ms, DPI={}, 文件: {}, 活动任务: {}",
                pageCount, elapsedTime, ConfigConstants.getOptimizedDpi(pageCount),
                pdfFilePath, activeTaskCount.get());

        return imageUrls;
    }

    /**
     * 检查线程池负载
     */
    private void checkThreadPoolLoad() {
        if (threadPoolExecutor instanceof ThreadPoolExecutor pool) {
            int activeCount = pool.getActiveCount();
            long taskCount = pool.getTaskCount();
            long completedTaskCount = pool.getCompletedTaskCount();
            int queueSize = pool.getQueue().size();

            logger.debug("线程池状态: 活动线程={}, 队列大小={}, 总任务={}, 已完成={}",
                    activeCount, queueSize, taskCount, completedTaskCount);

            if (queueSize > 50) {
                logger.warn("PDF转换任务队列堆积，当前队列大小: {}", queueSize);
            }
        }
    }

    /**
     * 高性能并行转换 - 独立加载每个批次（针对100页以上的大文件）
     */
    private List<String> convertHighPerformance(File pdfFile, String filePassword,
                                                String pdfFilePath, String folder, int pageCount) {
        List<String> imageUrls = Collections.synchronizedList(new ArrayList<>(pageCount));
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        int batchCount = (pageCount + BATCH_SIZE - 1) / BATCH_SIZE;
        long[] totalBatchTime = new long[]{0};

        logger.info("使用高性能独立加载并行转换，总页数: {}, 批次数: {}, DPI: {}, 超时: {}秒",
                pageCount, batchCount, ConfigConstants.getOptimizedDpi(pageCount),
                calculateTimeout(pageCount));

        List<CompletableFuture<Void>> batchFutures = new ArrayList<>();

        for (int batchIndex = 0; batchIndex < batchCount; batchIndex++) {
            final int currentBatch = batchIndex;
            final int batchStart = batchIndex * BATCH_SIZE;
            final int batchEnd = Math.min(batchStart + BATCH_SIZE, pageCount);

            CompletableFuture<Void> batchFuture = CompletableFuture.runAsync(() -> {
                activeTaskCount.incrementAndGet();
                long batchStartTime = System.currentTimeMillis();
                try {
                    try (PDDocument batchDoc = Loader.loadPDF(pdfFile, filePassword)) {
                        batchDoc.setResourceCache(new NotResourceCache());
                        PDFRenderer renderer = new PDFRenderer(batchDoc);
                        renderer.setSubsamplingAllowed(true);

                        // 直接使用配置的DPI值
                        int dpi = ConfigConstants.getOptimizedDpi(pageCount);

                        int pagesInBatch = 0;
                        for (int pageIndex = batchStart; pageIndex < batchEnd; pageIndex++) {
                            try {
                                String imageFilePath = folder + File.separator + pageIndex + PDF2JPG_IMAGE_FORMAT;
                                BufferedImage image = renderer.renderImageWithDPI(
                                        pageIndex,
                                        dpi,
                                        ImageType.RGB
                                );

                                ImageIOUtil.writeImage(image, imageFilePath, dpi);
                                image.flush();

                                String imageUrl = fileHandlerService.getPdf2jpgUrl(pdfFilePath, pageIndex);
                                synchronized (imageUrls) {
                                    imageUrls.add(imageUrl);
                                }

                                successCount.incrementAndGet();
                                pagesInBatch++;

                            } catch (Exception e) {
                                errorCount.incrementAndGet();
                                logger.error("转换页 {} 失败: {}", pageIndex, e.getMessage());
                            }
                        }

                        long batchTime = System.currentTimeMillis() - batchStartTime;
                        synchronized (this) {
                            totalBatchTime[0] += batchTime;
                        }

                        if (logger.isDebugEnabled()) {
                            logger.debug("批次{}完成: 转换{}页, 耗时: {}ms",
                                    currentBatch, pagesInBatch, batchTime);
                        }
                    }
                } catch (Exception e) {
                    logger.error("批次{}处理失败: {}", currentBatch, e.getMessage());
                    errorCount.addAndGet(batchEnd - batchStart);
                } finally {
                    activeTaskCount.decrementAndGet();
                    totalCompletedTasks.incrementAndGet();
                }
            }, threadPoolExecutor);

            batchFutures.add(batchFuture);
        }

        // 等待所有批次完成
        int timeout = calculateTimeout(pageCount);
        long waitStartTime = System.currentTimeMillis();

        try {
            CompletableFuture<Void> allBatches = CompletableFuture.allOf(
                    batchFutures.toArray(new CompletableFuture[0])
            );
            allBatches.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            logger.warn("PDF转换超时，已转换页数: {}，超时时间: {}秒", successCount.get(), timeout);
        } catch (Exception e) {
            logger.error("批量转换失败", e);
        }

        long waitTime = System.currentTimeMillis() - waitStartTime;

        logger.info("批次转换统计: 总批次={}, 成功={}, 失败={}, DPI={}, 等待耗时={}ms",
                batchCount, successCount.get(), errorCount.get(),
                ConfigConstants.getOptimizedDpi(pageCount), waitTime);

        // 按页码排序
        return sortImageUrls(imageUrls);
    }

    /**
     * 优化并行转换 - 线程安全的批处理模式（针对100页以内的文件）
     */
    private List<String> convertOptimizedParallel(File pdfFile, String filePassword,
                                                  String pdfFilePath, String folder, int pageCount) {
        int dpi = ConfigConstants.getOptimizedDpi(pageCount);

        logger.info("使用高性能批处理并行转换，总页数: {}, DPI: {}, 超时: {}秒",
                pageCount, dpi, calculateTimeout(pageCount));

        // 按CPU核心数划分批次，优化并行度
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int optimalBatchSize = Math.max(1, pageCount / availableProcessors);
        optimalBatchSize = Math.min(optimalBatchSize, 10); // 每批最多10页

        logger.debug("可用处理器: {}, 推荐批次大小: {}", availableProcessors, optimalBatchSize);

        List<CompletableFuture<List<String>>> batchFutures = new ArrayList<>();
        List<String> allImageUrls = Collections.synchronizedList(new ArrayList<>(pageCount));

        // 分批次并行处理
        for (int batchStart = 0; batchStart < pageCount; batchStart += optimalBatchSize) {
            final int startPage = batchStart;
            final int endPage = Math.min(batchStart + optimalBatchSize, pageCount);

            CompletableFuture<List<String>> batchFuture = CompletableFuture.supplyAsync(() -> {
                List<String> batchImageUrls = new ArrayList<>(endPage - startPage);
                activeTaskCount.incrementAndGet();

                try {
                    // 每个批次独立加载PDF，处理一批页面（而不是一页）
                    try (PDDocument batchDoc = Loader.loadPDF(pdfFile, filePassword)) {
                        batchDoc.setResourceCache(new NotResourceCache());
                        PDFRenderer renderer = new PDFRenderer(batchDoc);
                        renderer.setSubsamplingAllowed(true);

                        for (int pageIndex = startPage; pageIndex < endPage; pageIndex++) {
                            try {
                                String imageFilePath = folder + File.separator + pageIndex + PDF2JPG_IMAGE_FORMAT;
                                BufferedImage image = renderer.renderImageWithDPI(
                                        pageIndex,
                                        dpi,
                                        ImageType.RGB
                                );

                                ImageIOUtil.writeImage(image, imageFilePath, dpi);
                                image.flush();

                                String imageUrl = fileHandlerService.getPdf2jpgUrl(pdfFilePath, pageIndex);
                                batchImageUrls.add(imageUrl);

                            } catch (Exception e) {
                                logger.error("批次内转换页 {} 失败: {}", pageIndex, e.getMessage());
                                // 添加占位符URL
                                String placeholderUrl = fileHandlerService.getPdf2jpgUrl(pdfFilePath, pageIndex);
                                batchImageUrls.add(placeholderUrl);
                            }
                        }

                        if (logger.isDebugEnabled()) {
                            logger.debug("批次 {}-{} 完成，转换 {} 页",
                                    startPage, endPage - 1, batchImageUrls.size());
                        }
                    }
                } catch (Exception e) {
                    logger.error("批次 {}-{} 加载失败: {}", startPage, endPage - 1, e.getMessage());
                    // 为整个批次添加占位符URL
                    for (int pageIndex = startPage; pageIndex < endPage; pageIndex++) {
                        batchImageUrls.add(fileHandlerService.getPdf2jpgUrl(pdfFilePath, pageIndex));
                    }
                } finally {
                    activeTaskCount.decrementAndGet();
                    totalCompletedTasks.incrementAndGet();
                }

                return batchImageUrls;
            }, threadPoolExecutor);

            batchFutures.add(batchFuture);
        }

        // 等待所有批次完成并收集结果
        CompletableFuture<Void> allBatches = CompletableFuture.allOf(
                batchFutures.toArray(new CompletableFuture[0])
        );

        int timeout = calculateTimeout(pageCount);
        try {
            allBatches.get(timeout, TimeUnit.SECONDS);

            // 收集所有批次的结果
            for (CompletableFuture<List<String>> future : batchFutures) {
                try {
                    List<String> batchUrls = future.getNow(null);
                    if (batchUrls != null) {
                        allImageUrls.addAll(batchUrls);
                    }
                } catch (Exception e) {
                    // 忽略已完成的任务
                }
            }

        } catch (TimeoutException e) {
            logger.warn("PDF转换超时，已转换页数: {}，超时时间: {}秒", allImageUrls.size(), timeout);
        } catch (Exception e) {
            logger.error("批次并行转换失败", e);
        }

        // 确保返回正确数量的URL
        return sortImageUrls(allImageUrls);
    }

    /**
     * 处理PDF加载异常
     */
    private void handlePdfLoadException(Exception e, String pdfFilePath) throws Exception {
        Throwable[] throwableArray = ExceptionUtils.getThrowables(e);
        for (Throwable throwable : throwableArray) {
            if (throwable instanceof IOException || throwable instanceof EncryptedDocumentException) {
                if (e.getMessage().toLowerCase().contains(PDF_PASSWORD_MSG)) {
                    logger.info("PDF文件需要密码: {}", pdfFilePath);
                    throw new Exception(PDF_PASSWORD_MSG, e);
                }
            }
        }
        logger.error("加载PDF文件异常, pdfFilePath：{}", pdfFilePath, e);
        throw new Exception("PDF文件加载失败", e);
    }

    /**
     * 计算超时时间 - 标准化配置，不使用计算
     */
    private int calculateTimeout(int pageCount) {
        // 根据页数范围直接返回对应的超时时间配置
        if (pageCount <= 50) {
            return ConfigConstants.getPdfTimeoutSmall();      // 小文件：90秒
        } else if (pageCount <= 200) {
            return ConfigConstants.getPdfTimeoutMedium();     // 中等文件：180秒
        } else if (pageCount <= 500) {
            return ConfigConstants.getPdfTimeoutLarge();      // 大文件：300秒
        } else {
            return ConfigConstants.getPdfTimeoutXLarge();     // 超大文件：600秒
        }
    }

    /**
     * 按页码排序
     */
    private List<String> sortImageUrls(List<String> imageUrls) {
        List<String> sortedImageUrls = new ArrayList<>(imageUrls);
        sortedImageUrls.sort((url1, url2) -> {
            try {
                String pageStr1 = url1.substring(url1.lastIndexOf('/') + 1, url1.lastIndexOf('.'));
                String pageStr2 = url2.substring(url2.lastIndexOf('/') + 1, url2.lastIndexOf('.'));
                return Integer.compare(Integer.parseInt(pageStr1), Integer.parseInt(pageStr2));
            } catch (Exception e) {
                return 0;
            }
        });
        return sortedImageUrls;
    }

}