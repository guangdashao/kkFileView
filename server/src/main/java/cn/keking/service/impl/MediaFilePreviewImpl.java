package cn.keking.service.impl;

import cn.keking.config.ConfigConstants;
import cn.keking.model.FileAttribute;
import cn.keking.model.FileType;
import cn.keking.model.ReturnResponse;
import cn.keking.service.FileHandlerService;
import cn.keking.service.FilePreview;
import cn.keking.service.Mediatomp4Service;
import cn.keking.utils.DownloadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author : kl
 * @authorboke : kailing.pub
 * @create : 2018-03-25 上午11:58
 * @description:
 **/
@Service
public class MediaFilePreviewImpl implements FilePreview {

    private static final Logger logger = LoggerFactory.getLogger(MediaFilePreviewImpl.class);
    private final FileHandlerService fileHandlerService;
    private final OtherFilePreviewImpl otherFilePreview;

    public MediaFilePreviewImpl(FileHandlerService fileHandlerService, OtherFilePreviewImpl otherFilePreview) {
        this.fileHandlerService = fileHandlerService;
        this.otherFilePreview = otherFilePreview;
    }

    @Override
    public String filePreviewHandle(String url, Model model, FileAttribute fileAttribute) {
        String fileName = fileAttribute.getName();
        String suffix = fileAttribute.getSuffix();
        String cacheName = fileAttribute.getCacheName();
        String outFilePath = fileAttribute.getOutFilePath();
        boolean forceUpdatedCache = fileAttribute.forceUpdatedCache();
        FileType type = fileAttribute.getType();
        String[] mediaTypesConvert = FileType.MEDIA_CONVERT_TYPES;  //获取支持的转换格式
        boolean mediaTypes = false;
        for (String temp : mediaTypesConvert) {
            if (suffix.equalsIgnoreCase(temp)) {
                mediaTypes = true;
                break;
            }
        }

        // 非HTTP协议或需要转换的文件
        if (!url.toLowerCase().startsWith("http") || checkNeedConvert(mediaTypes)) {
            // 检查缓存
            File outputFile = new File(outFilePath);
            if (outputFile.exists() && !forceUpdatedCache && ConfigConstants.isCacheEnabled()) {
                String relativePath = fileHandlerService.getRelativePath(outFilePath);
                if (fileHandlerService.listConvertedFiles().containsKey(cacheName)) {
                    model.addAttribute("mediaUrl", relativePath);
                    logger.info("使用已缓存的视频文件: {}", cacheName);
                    return MEDIA_FILE_PREVIEW_PAGE;
                }
            }

            // 下载文件
            ReturnResponse<String> response = DownloadUtils.downLoad(fileAttribute, fileName);
            if (response.isFailure()) {
                return otherFilePreview.notSupportedFile(model, fileAttribute, response.getMsg());
            }

            String filePath = response.getContent();

            try {
                if (mediaTypes) {
                    // 检查文件大小限制
                    if (isFileSizeExceeded(filePath)) {
                        return otherFilePreview.notSupportedFile(model, fileAttribute,
                                "视频文件大小超过" + ConfigConstants.getMediaConvertMaxSize() + "MB限制，禁止转换");
                    }

                    // 使用改进的转换方法
                    String convertedPath = convertVideoWithImprovedTimeout(filePath, outFilePath, fileAttribute);
                    if (convertedPath != null) {
                        model.addAttribute("mediaUrl", fileHandlerService.getRelativePath(convertedPath));

                        // 缓存转换结果
                        if (ConfigConstants.isCacheEnabled()) {
                            fileHandlerService.addConvertedFile(cacheName, fileHandlerService.getRelativePath(convertedPath));
                        }
                        return MEDIA_FILE_PREVIEW_PAGE;
                    } else {
                        return otherFilePreview.notSupportedFile(model, fileAttribute, "视频转换失败，请联系管理员");
                    }
                } else {
                    // 不需要转换的文件
                    model.addAttribute("mediaUrl", fileHandlerService.getRelativePath(outFilePath));
                    return MEDIA_FILE_PREVIEW_PAGE;
                }
            } catch (Exception e) {
                logger.error("处理媒体文件失败: {}", filePath, e);
                return otherFilePreview.notSupportedFile(model, fileAttribute,
                        "视频处理异常: " + getErrorMessage(e));
            }
        }

        // HTTP协议的媒体文件，直接播放
        if (type.equals(FileType.MEDIA)) {
            model.addAttribute("mediaUrl", url);
            return MEDIA_FILE_PREVIEW_PAGE;
        }

        return otherFilePreview.notSupportedFile(model, fileAttribute, "系统还不支持该格式文件的在线预览");
    }

    /**
     * 检查文件大小是否超过限制
     */
    private boolean isFileSizeExceeded(String filePath) {
        try {
            File inputFile = new File(filePath);
            if (inputFile.exists()) {
                long fileSizeMB = inputFile.length() / (1024 * 1024);
                int maxSizeMB = ConfigConstants.getMediaConvertMaxSize();

                if (fileSizeMB > maxSizeMB) {
                    logger.warn("视频文件大小超过限制: {}MB > {}MB", fileSizeMB, maxSizeMB);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("检查文件大小时出错: {}", filePath, e);
        }
        return false;
    }

    /**
     * 改进的转换方法
     */
    private String convertVideoWithImprovedTimeout(String filePath, String outFilePath,
                                                   FileAttribute fileAttribute) {
        try {
            // 检查文件是否存在
            File outputFile = new File(outFilePath);
            if (outputFile.exists() && !fileAttribute.forceUpdatedCache()) {
                logger.info("输出文件已存在且非强制更新模式，跳过转换!");
                return outFilePath;
            }

            // 使用改进的异步转换方法
            CompletableFuture<Boolean> future =
                    Mediatomp4Service.convertToMp4Async(filePath, outFilePath, fileAttribute);

            // 计算超时时间
            File inputFile = new File(filePath);
            long fileSizeMB = inputFile.length() / (1024 * 1024);
            int timeoutSeconds = calculateTimeout(fileSizeMB);

            try {
                boolean result = future.get(timeoutSeconds, TimeUnit.SECONDS);
                if (result) {
                    // 验证输出文件
                    File convertedFile = new File(outFilePath);
                    if (!convertedFile.exists() || convertedFile.length() == 0) {
                        throw new IOException("转换完成但输出文件无效");
                    }
                    return outFilePath;
                } else {
                    throw new Exception("转换返回失败状态");
                }
            } catch (TimeoutException e) {
                // 超时后尝试获取任务ID并取消
                logger.error("视频转换超时: {}, 文件大小: {}MB, 超时: {}秒",
                        filePath, fileSizeMB, timeoutSeconds);

                throw new RuntimeException("视频转换超时，文件可能过大");
            }

        } catch (Exception e) {
            logger.error("视频转换异常: {}", filePath, e);
            throw new RuntimeException("视频转换失败: " + getErrorMessage(e), e);
        }
    }

    /**
     * 计算超时时间 - 从配置文件读取
     */
    public int calculateTimeout(long fileSizeMB) {
        // 如果超时功能被禁用，返回一个非常大的值
        if (!ConfigConstants.isMediaTimeoutEnabled()) {
            return Integer.MAX_VALUE;
        }

        // 根据文件大小从配置文件读取超时时间
        if (fileSizeMB < 10) return ConfigConstants.getMediaSmallFileTimeout();    // 小文件
        if (fileSizeMB < 50) return ConfigConstants.getMediaMediumFileTimeout();   // 中等文件
        if (fileSizeMB < 200) return ConfigConstants.getMediaLargeFileTimeout();   // 较大文件
        if (fileSizeMB < 500) return ConfigConstants.getMediaXLFileTimeout();      // 大文件
        if (fileSizeMB < 1024) return ConfigConstants.getMediaXXLFileTimeout();    // 超大文件
        return ConfigConstants.getMediaXXXLFileTimeout();                           // 极大文件
    }

    /**
     * 检查是否需要转换
     */
    private boolean checkNeedConvert(boolean mediaTypes) {
        // 1.检查开关是否开启
        if ("true".equals(ConfigConstants.getMediaConvertDisable())) {
            return mediaTypes;
        }
        return false;
    }

    /**
     * 获取友好的错误信息
     */
    private String getErrorMessage(Exception e) {
        if (e instanceof CancellationException) {
            return "转换被取消";
        } else if (e instanceof TimeoutException) {
            return "转换超时";
        } else if (e.getMessage() != null) {
            // 截取主要错误信息
            String msg = e.getMessage();
            if (msg.length() > 100) {
                msg = msg.substring(0, 100) + "...";
            }
            return msg;
        }
        return "未知错误";
    }
}