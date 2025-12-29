package cn.keking.utils;

import cn.keking.config.ConfigConstants;
import cn.keking.service.FileHandlerService;
import cn.keking.web.filter.BaseUrlFilter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.io.FileChannelRandomAccessSource;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import org.apache.commons.imaging.Imaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ConvertPicUtil {

    private static final int FIT_WIDTH = 500;
    private static final int FIT_HEIGHT = 900;
    private final static Logger logger = LoggerFactory.getLogger(ConvertPicUtil.class);
    private final static String fileDir = ConfigConstants.getFileDir();
    /**
     * Tif 转  JPG。
     *
     * @param strInputFile  输入文件的路径和文件名
     * @param strOutputFile 输出文件的路径和文件名
     * @return boolean 是否转换成功
     */
    public static List<String> convertTif2Jpg(String strInputFile, String strOutputFile, boolean forceUpdatedCache) throws Exception {
        List<String> listImageFiles = new ArrayList<>();
        String baseUrl = BaseUrlFilter.getBaseUrl();
        strOutputFile = strOutputFile.substring(0, strOutputFile.lastIndexOf('.'));

        File tiffFile = new File(strInputFile);
        if (!tiffFile.exists()) {
            logger.info("找不到文件【{}】", strInputFile);
            return null;
        }

        File outputDir = new File(strOutputFile);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("创建目录失败: " + strOutputFile);
        }

        List<BufferedImage> images;
        try {
            images = Imaging.getAllBufferedImages(tiffFile);
            for (int i = 0; i < images.size(); i++) {
                String fileName = strOutputFile + File.separator + i + ".jpg";
                File outputFile = new File(fileName);

                // 如果需要强制更新缓存或者文件不存在，则转换
                if (forceUpdatedCache || !outputFile.exists()) {
                    BufferedImage image = images.get(i);
                    // 写入JPG格式
                    boolean success = ImageIO.write(image, "png", outputFile);
                    if (!success) {
                        throw new IOException("无法写入JPG格式图片: " + fileName);
                    }
                    logger.debug("转换图片: {}", fileName);
                } else {
                    logger.debug("使用缓存图片: {}", fileName);
                }

                // 构建URL
                String relativePath = fileName.replace(fileDir, "");
                String url = baseUrl + WebUtils.encodeFileName(relativePath);
                listImageFiles.add(url);
            }
        } catch (IOException e) {
            if (!e.getMessage().contains("Only sequential, baseline JPEGs are supported at the moment")) {
                logger.error("TIF转JPG异常，文件路径：{}", strInputFile, e);
            }
            throw new Exception(e);
        }
        return listImageFiles;
    }


    /**
     * 将Jpg图片转换为Pdf文件
     *
     * @param strJpgFile 输入的jpg的路径和文件名
     * @param strPdfFile 输出的pdf的路径和文件名
     */
    public static String convertJpg2Pdf(String strJpgFile, String strPdfFile) throws Exception {
        Document document = new Document();
        RandomAccessFileOrArray rafa = null;
        FileOutputStream outputStream = null;
        try {
            RandomAccessFile aFile = new RandomAccessFile(strJpgFile, "r");
            FileChannel inChannel = aFile.getChannel();
            FileChannelRandomAccessSource fcra =  new FileChannelRandomAccessSource(inChannel);
            rafa = new RandomAccessFileOrArray(fcra);
            int pages = TiffImage.getNumberOfPages(rafa);
            outputStream = new FileOutputStream(strPdfFile);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            Image image;
            for (int i = 1; i <= pages; i++) {
                image = TiffImage.getTiffImage(rafa, i);
                image.scaleToFit(FIT_WIDTH, FIT_HEIGHT);
                document.add(image);
            }
        } catch (IOException e) {
            if (!e.getMessage().contains("Bad endianness tag (not 0x4949 or 0x4d4d)") ) {
                logger.error("TIF转JPG异常，文件路径：" + strPdfFile, e);
            }
            throw new Exception(e);
        } finally {
            if (document != null) {
                document.close();
            }
            if (rafa != null) {
                rafa.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return strPdfFile;
    }
}