package cn.keking.config;

import cn.keking.utils.ConfigUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author: chenjh
 * @since: 2019/4/10 17:22
 */
@Component(value = ConfigConstants.BEAN_NAME)
public class ConfigConstants {
    public static final String BEAN_NAME = "configConstants";

    static {
        // PDFBox兼容低版本JDK
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
    }

    // ==================================================
    // 常量定义区
    // ==================================================

    // 缓存配置常量
    public static final String DEFAULT_CACHE_ENABLED = "true";

    // 文件类型配置常量
    public static final String DEFAULT_TXT_TYPE = "txt,html,htm,asp,jsp,xml,json,properties,md,gitignore,log,java,py,c,cpp,sql,sh,bat,m,bas,prg,cmd,xbrl";
    public static final String DEFAULT_MEDIA_TYPE = "mp3,wav,mp4,flv";
    public static final String DEFAULT_PROHIBIT = "exe,dll";
    public static final String DEFAULT_TIF_PREVIEW_TYPE = "tif";
    public static final String DEFAULT_CAD_PREVIEW_TYPE = "pdf";

    // Office配置常量
    public static final String DEFAULT_OFFICE_PREVIEW_TYPE = "image";
    public static final String DEFAULT_OFFICE_PREVIEW_SWITCH_DISABLED = "false";
    public static final String DEFAULT_OFFICE_TYPE_WEB = "web";
    public static final String DEFAULT_OFFICE_PAQERANQE = "false";
    public static final String DEFAULT_OFFICE_WATERMARK = "false";
    public static final String DEFAULT_OFFICE_QUALITY = "80";
    public static final String DEFAULT_OFFICE_MAXIMAQERESOLUTION = "150";
    public static final String DEFAULT_OFFICE_EXPORTBOOKMARKS = "true";
    public static final String DEFAULT_OFFICE_EXPORTNOTES = "true";
    public static final String DEFAULT_OFFICE_EOCUMENTOPENPASSWORDS = "true";

    // FTP配置常量
    public static final String DEFAULT_FTP_USERNAME = null;
    public static final String DEFAULT_FTP_PASSWORD = null;
    public static final String DEFAULT_FTP_CONTROL_ENCODING = "UTF-8";

    // 路径配置常量
    public static final String DEFAULT_VALUE = "default";

    // PDF配置常量
    public static final String DEFAULT_PDF_PRESENTATION_MODE_DISABLE = "true";
    public static final String DEFAULT_PDF_OPEN_FILE_DISABLE = "true";
    public static final String DEFAULT_PDF_PRINT_DISABLE = "true";
    public static final String DEFAULT_PDF_DOWNLOAD_DISABLE = "true";
    public static final String DEFAULT_PDF_BOOKMARK_DISABLE = "true";
    public static final String DEFAULT_PDF_DISABLE_EDITING = "true";
    public static final String DEFAULT_PDF2_JPG_DPI = "105";
    public static final String DEFAULT_PDF_TIMEOUT = "90";
    public static final String DEFAULT_PDF_TIMEOUT80 = "180";
    public static final String DEFAULT_PDF_TIMEOUT200 = "300";
    public static final String DEFAULT_PDF_THREAD = "5";

    // CAD配置常量
    public static final String DEFAULT_CAD_TIMEOUT = "90";
    public static final String DEFAULT_CAD_THREAD = "5";

    // 文件操作配置常量
    public static final String DEFAULT_FILE_UPLOAD_DISABLE = "false";
    public static final String DEFAULT_DELETE_SOURCE_FILE = "true";
    public static final String DEFAULT_DELETE_CAPTCHA = "false";
    public static final String DEFAULT_SIZE = "500MB";
    public static final String DEFAULT_PASSWORD = "123456";

    // 首页配置常量
    public static final String DEFAULT_BEIAN = "无";
    public static final String DEFAULT_HOME_PAGENUMBER = "1";
    public static final String DEFAULT_HOME_PAGINATION = "true";
    public static final String DEFAULT_HOME_PAGSIZE = "15";
    public static final String DEFAULT_HOME_SEARCH = "true";

    // 权限配置常量
    public static final String DEFAULT_KEY = "false";
    public static final String DEFAULT_PICTURES_PREVIEW = "true";
    public static final String DEFAULT_GET_CORS_FILE = "true";
    public static final String DEFAULT_ADD_TASK = "true";
    public static final String DEFAULT_AES_KEY= "1234567890123456";

    // UserAgent配置常量
    public static final String DEFAULT_USER_AGENT = "false";

    // Basic认证配置常量
    public static final String DEFAULT_BASIC_NAME = "";

    // ==================================================
    // 配置变量定义区（按功能分类）
    // ==================================================

    // 1. 缓存配置
    private static Boolean cacheEnabled;

    // 2. 文件类型配置
    private static String[] simTexts = {};
    private static String[] medias = {};
    private static String[] convertMedias = {};
    private static String[] prohibit = {};
    private static String mediaConvertDisable;
    private static String tifPreviewType;
    private static String cadPreviewType;

    // 3. Office配置
    private static String officePreviewType;
    private static String officePreviewSwitchDisabled;
    private static String officeTypeWeb;
    private static String officePageRange;
    private static String officeWatermark;
    private static String officeQuality;
    private static String officeMaxImageResolution;
    private static Boolean officeExportBookmarks;
    private static Boolean officeExportNotes;
    private static Boolean officeDocumentOpenPasswords;

    // 4. FTP配置
    private static String ftpUsername;
    private static String ftpPassword;
    private static String ftpControlEncoding;

    // 5. 路径配置
    private static String fileDir = ConfigUtils.getHomePath() + File.separator + "file" + File.separator;
    private static String localPreviewDir;
    private static String baseUrl;

    // 6. 安全配置
    private static CopyOnWriteArraySet<String> trustHostSet;
    private static CopyOnWriteArraySet<String> notTrustHostSet;

    // 7. PDF配置
    private static String pdfPresentationModeDisable;
    private static String pdfDisableEditing;
    private static String pdfOpenFileDisable;
    private static String pdfPrintDisable;
    private static String pdfDownloadDisable;
    private static String pdfBookmarkDisable;
    private static int pdf2JpgDpi;
    private static int pdfTimeout;
    private static int pdfTimeout80;
    private static int pdfTimeout200;
    private static int pdfThread;

    // 8. CAD配置
    private static String cadTimeout;
    private static int cadThread;

    // 9. 文件操作配置
    private static Boolean fileUploadDisable;
    private static String size;
    private static String password;
    private static Boolean deleteSourceFile;
    private static Boolean deleteCaptcha;

    // 10. 首页配置
    private static String beian;
    private static String homePageNumber;
    private static String homePagination;
    private static String homePageSize;
    private static String homeSearch;

    // 11. 权限配置
    private static String key;
    private static boolean picturesPreview;
    private static boolean getCorsFile;
    private static boolean addTask;
    private static String aesKey;

    // 12. UserAgent配置
    private static String userAgent;

    // 13. Basic认证配置
    private static String basicName;

    // ==================================================
    // 获取方法（按功能分类）
    // ==================================================

    // 1. 缓存配置获取方法
    public static Boolean isCacheEnabled() {
        return cacheEnabled;
    }

    // 2. 文件类型配置获取方法
    public static String[] getSimText() {
        return simTexts;
    }

    public static String[] getMedia() {
        return medias;
    }

    public static String[] getConvertMedias() {
        return convertMedias;
    }

    public static String getMediaConvertDisable() {
        return mediaConvertDisable;
    }

    public static String getTifPreviewType() {
        return tifPreviewType;
    }

    public static String[] getProhibit() {
        return prohibit;
    }

    // 3. Office配置获取方法
    public static String getOfficePreviewType() {
        return officePreviewType;
    }

    public static String getOfficePreviewSwitchDisabled() {
        return officePreviewSwitchDisabled;
    }

    public static String getOfficeTypeWeb() {
        return officeTypeWeb;
    }

    public static String getOfficePageRange() {
        return officePageRange;
    }

    public static String getOfficeWatermark() {
        return officeWatermark;
    }

    public static String getOfficeQuality() {
        return officeQuality;
    }

    public static String getOfficeMaxImageResolution() {
        return officeMaxImageResolution;
    }

    public static Boolean getOfficeExportBookmarks() {
        return officeExportBookmarks;
    }

    public static Boolean getOfficeExportNotes() {
        return officeExportNotes;
    }

    public static Boolean getOfficeDocumentOpenPasswords() {
        return officeDocumentOpenPasswords;
    }

    // 4. FTP配置获取方法
    public static String getFtpUsername() {
        return ftpUsername;
    }

    public static String getFtpPassword() {
        return ftpPassword;
    }

    public static String getFtpControlEncoding() {
        return ftpControlEncoding;
    }

    // 5. 路径配置获取方法
    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getFileDir() {
        return fileDir;
    }

    public static String getLocalPreviewDir() {
        return localPreviewDir;
    }

    // 6. 安全配置获取方法
    public static Set<String> getTrustHostSet() {
        return trustHostSet;
    }

    public static Set<String> getNotTrustHostSet() {
        return notTrustHostSet;
    }

    // 7. PDF配置获取方法
    public static String getPdfPresentationModeDisable() {
        return pdfPresentationModeDisable;
    }

    public static String getPdfOpenFileDisable() {
        return pdfOpenFileDisable;
    }

    public static String getPdfPrintDisable() {
        return pdfPrintDisable;
    }

    public static String getPdfDownloadDisable() {
        return pdfDownloadDisable;
    }

    public static String getPdfBookmarkDisable() {
        return pdfBookmarkDisable;
    }

    public static String getPdfDisableEditing() {
        return pdfDisableEditing;
    }

    public static int getPdf2JpgDpi() {
        return pdf2JpgDpi;
    }

    public static int getPdfTimeout() {
        return pdfTimeout;
    }

    public static int getPdfTimeout80() {
        return pdfTimeout80;
    }

    public static int getPdfTimeout200() {
        return pdfTimeout200;
    }

    public static int getPdfThread() {
        return pdfThread;
    }

    // 8. CAD配置获取方法
    public static String getCadPreviewType() {
        return cadPreviewType;
    }

    public static String getCadTimeout() {
        return cadTimeout;
    }

    public static int getCadThread() {
        return cadThread;
    }

    // 9. 文件操作配置获取方法
    public static Boolean getFileUploadDisable() {
        return fileUploadDisable;
    }

    public static String maxSize() {
        return size;
    }

    public static String getPassword() {
        return password;
    }

    public static Boolean getDeleteSourceFile() {
        return deleteSourceFile;
    }

    public static Boolean getDeleteCaptcha() {
        return deleteCaptcha;
    }

    // 10. 首页配置获取方法
    public static String getBeian() {
        return beian;
    }

    public static String getHomePageNumber() {
        return homePageNumber;
    }

    public static String getHomePagination() {
        return homePagination;
    }

    public static String getHomePageSize() {
        return homePageSize;
    }

    public static String getHomeSearch() {
        return homeSearch;
    }

    // 11. 权限配置获取方法
    public static String getKey() {
        return key;
    }

    public static boolean getPicturesPreview() {
        return picturesPreview;
    }

    public static boolean getGetCorsFile() {
        return getCorsFile;
    }

    public static boolean getAddTask() {
        return addTask;
    }

    public static String getaesKey() {
        return aesKey;
    }

    // 12. UserAgent配置获取方法
    public static String getUserAgent() {
        return userAgent;
    }

    // 13. Basic认证配置获取方法
    public static String getBasicName() {
        return basicName;
    }

    // ==================================================
    // Setter方法（按功能分类）
    // ==================================================

    // 1. 缓存配置Setter方法
    @Value("${cache.enabled:true}")
    public void setCacheEnabled(String cacheEnabled) {
        setCacheEnabledValueValue(Boolean.parseBoolean(cacheEnabled));
    }

    public static void setCacheEnabledValueValue(Boolean cacheEnabled) {
        ConfigConstants.cacheEnabled = cacheEnabled;
    }

    // 2. 文件类型配置Setter方法
    @Value("${simText:txt,html,htm,asp,jsp,xml,json,properties,md,gitignore,log,java,py,c,cpp,sql,sh,bat,m,bas,prg,cmd,xbrl}")
    public void setSimText(String simText) {
        String[] simTextArr = simText.split(",");
        setSimTextValue(simTextArr);
    }

    public static void setSimTextValue(String[] simText) {
        ConfigConstants.simTexts = simText;
    }

    @Value("${media:mp3,wav,mp4,flv}")
    public void setMedia(String media) {
        String[] mediaArr = media.split(",");
        setMediaValue(mediaArr);
    }

    public static void setMediaValue(String[] Media) {
        ConfigConstants.medias = Media;
    }

    @Value("${convertMedias:avi,mov,wmv,mkv,3gp,rm}")
    public void setConvertMedias(String convertMedia) {
        String[] mediaArr = convertMedia.split(",");
        setConvertMediaValue(mediaArr);
    }

    public static void setConvertMediaValue(String[] ConvertMedia) {
        ConfigConstants.convertMedias = ConvertMedia;
    }

    @Value("${media.convert.disable:true}")
    public void setMediaConvertDisable(String mediaConvertDisable) {
        setMediaConvertDisableValue(mediaConvertDisable);
    }

    public static void setMediaConvertDisableValue(String mediaConvertDisable) {
        ConfigConstants.mediaConvertDisable = mediaConvertDisable;
    }

    @Value("${tif.preview.type:tif}")
    public void setTifPreviewType(String tifPreviewType) {
        setTifPreviewTypeValue(tifPreviewType);
    }

    public static void setTifPreviewTypeValue(String tifPreviewType) {
        ConfigConstants.tifPreviewType = tifPreviewType;
    }

    @Value("${cad.preview.type:svg}")
    public void setCadPreviewType(String cadPreviewType) {
        setCadPreviewTypeValue(cadPreviewType);
    }

    public static void setCadPreviewTypeValue(String cadPreviewType) {
        ConfigConstants.cadPreviewType = cadPreviewType;
    }

    @Value("${prohibit:exe,dll}")
    public void setProhibit(String prohibit) {
        String[] prohibitArr = prohibit.split(",");
        setProhibitValue(prohibitArr);
    }

    public static void setProhibitValue(String[] prohibit) {
        ConfigConstants.prohibit = prohibit;
    }

    // 3. Office配置Setter方法
    @Value("${office.preview.type:image}")
    public void setOfficePreviewType(String officePreviewType) {
        setOfficePreviewTypeValue(officePreviewType);
    }

    public static void setOfficePreviewTypeValue(String officePreviewType) {
        ConfigConstants.officePreviewType = officePreviewType;
    }

    @Value("${office.preview.switch.disabled:true}")
    public void setOfficePreviewSwitchDisabled(String officePreviewSwitchDisabled) {
        ConfigConstants.officePreviewSwitchDisabled = officePreviewSwitchDisabled;
    }

    public static void setOfficePreviewSwitchDisabledValue(String officePreviewSwitchDisabled) {
        ConfigConstants.officePreviewSwitchDisabled = officePreviewSwitchDisabled;
    }

    @Value("${office.type.web:web}")
    public void setOfficeTypeWeb(String officeTypeWeb) {
        setOfficeTypeWebValue(officeTypeWeb);
    }

    public static void setOfficeTypeWebValue(String officeTypeWeb) {
        ConfigConstants.officeTypeWeb = officeTypeWeb;
    }

    @Value("${office.pagerange:false}")
    public void setOfficePageRange(String officePageRange) {
        setOfficePageRangeValue(officePageRange);
    }

    public static void setOfficePageRangeValue(String officePageRange) {
        ConfigConstants.officePageRange = officePageRange;
    }

    @Value("${office.watermark:false}")
    public void setOfficeWatermark(String officeWatermark) {
        setOfficeWatermarkValue(officeWatermark);
    }

    public static void setOfficeWatermarkValue(String officeWatermark) {
        ConfigConstants.officeWatermark = officeWatermark;
    }

    @Value("${office.quality:80}")
    public void setOfficeQuality(String officeQuality) {
        setOfficeQualityValue(officeQuality);
    }

    public static void setOfficeQualityValue(String officeQuality) {
        ConfigConstants.officeQuality = officeQuality;
    }

    @Value("${office.maximageresolution:150}")
    public void setOfficeMaxImageResolution(String officeMaxImageResolution) {
        setOfficeMaxImageResolutionValue(officeMaxImageResolution);
    }

    public static void setOfficeMaxImageResolutionValue(String officeMaxImageResolution) {
        ConfigConstants.officeMaxImageResolution = officeMaxImageResolution;
    }

    @Value("${office.exportbookmarks:true}")
    public void setOfficeExportBookmarks(Boolean officeExportBookmarks) {
        setOfficeExportBookmarksValue(officeExportBookmarks);
    }

    public static void setOfficeExportBookmarksValue(Boolean officeExportBookmarks) {
        ConfigConstants.officeExportBookmarks = officeExportBookmarks;
    }

    @Value("${office.exportnotes:true}")
    public void setExportNotes(Boolean officeExportNotes) {
        setOfficeExportNotesValue(officeExportNotes);
    }

    public static void setOfficeExportNotesValue(Boolean officeExportNotes) {
        ConfigConstants.officeExportNotes = officeExportNotes;
    }

    @Value("${office.documentopenpasswords:true}")
    public void setDocumentOpenPasswords(Boolean officeDocumentOpenPasswords) {
        setOfficeDocumentOpenPasswordsValue(officeDocumentOpenPasswords);
    }

    public static void setOfficeDocumentOpenPasswordsValue(Boolean officeDocumentOpenPasswords) {
        ConfigConstants.officeDocumentOpenPasswords = officeDocumentOpenPasswords;
    }

    // 4. FTP配置Setter方法
    @Value("${ftp.username:}")
    public void setFtpUsername(String ftpUsername) {
        setFtpUsernameValue(ftpUsername);
    }

    public static void setFtpUsernameValue(String ftpUsername) {
        ConfigConstants.ftpUsername = ftpUsername;
    }

    @Value("${ftp.password:}")
    public void setFtpPassword(String ftpPassword) {
        setFtpPasswordValue(ftpPassword);
    }

    public static void setFtpPasswordValue(String ftpPassword) {
        ConfigConstants.ftpPassword = ftpPassword;
    }

    @Value("${ftp.control.encoding:UTF-8}")
    public void setFtpControlEncoding(String ftpControlEncoding) {
        setFtpControlEncodingValue(ftpControlEncoding);
    }

    public static void setFtpControlEncodingValue(String ftpControlEncoding) {
        ConfigConstants.ftpControlEncoding = ftpControlEncoding;
    }

    // 5. 路径配置Setter方法
    @Value("${base.url:default}")
    public void setBaseUrl(String baseUrl) {
        setBaseUrlValue(baseUrl);
    }

    public static void setBaseUrlValue(String baseUrl) {
        ConfigConstants.baseUrl = baseUrl;
    }

    @Value("${file.dir:default}")
    public void setFileDir(String fileDir) {
        setFileDirValue(fileDir);
    }

    public static void setFileDirValue(String fileDir) {
        if (!DEFAULT_VALUE.equalsIgnoreCase(fileDir)) {
            if (!fileDir.endsWith(File.separator)) {
                fileDir = fileDir + File.separator;
            }
            ConfigConstants.fileDir = fileDir;
        }
    }

    @Value("${local.preview.dir:default}")
    public void setLocalPreviewDir(String localPreviewDir) {
        setLocalPreviewDirValue(localPreviewDir);
    }

    public static void setLocalPreviewDirValue(String localPreviewDir) {
        if (!DEFAULT_VALUE.equals(localPreviewDir)) {
            if (!localPreviewDir.endsWith(File.separator)) {
                localPreviewDir = localPreviewDir + File.separator;
            }
        }
        ConfigConstants.localPreviewDir = localPreviewDir;
    }

    // 6. 安全配置Setter方法
    @Value("${trust.host:default}")
    public void setTrustHost(String trustHost) {
        setTrustHostSet(getHostValue(trustHost));
    }

    public static void setTrustHostValue(String trustHost) {
        setTrustHostSet(getHostValue(trustHost));
    }

    @Value("${not.trust.host:default}")
    public void setNotTrustHost(String notTrustHost) {
        setNotTrustHostSet(getHostValue(notTrustHost));
    }

    public static void setNotTrustHostValue(String notTrustHost) {
        setNotTrustHostSet(getHostValue(notTrustHost));
    }

    private static CopyOnWriteArraySet<String> getHostValue(String trustHost) {
        if (DEFAULT_VALUE.equalsIgnoreCase(trustHost)) {
            return new CopyOnWriteArraySet<>();
        } else {
            // 去除空格并转小写
            String[] trustHostArray = trustHost.toLowerCase().replaceAll("\\s+", "").split(",");
            return new CopyOnWriteArraySet<>(Arrays.asList(trustHostArray));
        }
    }

    private static void setTrustHostSet(CopyOnWriteArraySet<String> trustHostSet) {
        ConfigConstants.trustHostSet = trustHostSet;
    }

    public static void setNotTrustHostSet(CopyOnWriteArraySet<String> notTrustHostSet) {
        ConfigConstants.notTrustHostSet = notTrustHostSet;
    }

    // 7. PDF配置Setter方法
    @Value("${pdf.presentationMode.disable:true}")
    public void setPdfPresentationModeDisable(String pdfPresentationModeDisable) {
        setPdfPresentationModeDisableValue(pdfPresentationModeDisable);
    }

    public static void setPdfPresentationModeDisableValue(String pdfPresentationModeDisable) {
        ConfigConstants.pdfPresentationModeDisable = pdfPresentationModeDisable;
    }

    @Value("${pdf.openFile.disable:true}")
    public void setPdfOpenFileDisable(String pdfOpenFileDisable) {
        setPdfOpenFileDisableValue(pdfOpenFileDisable);
    }

    public static void setPdfOpenFileDisableValue(String pdfOpenFileDisable) {
        ConfigConstants.pdfOpenFileDisable = pdfOpenFileDisable;
    }

    @Value("${pdf.print.disable:true}")
    public void setPdfPrintDisable(String pdfPrintDisable) {
        setPdfPrintDisableValue(pdfPrintDisable);
    }

    public static void setPdfPrintDisableValue(String pdfPrintDisable) {
        ConfigConstants.pdfPrintDisable = pdfPrintDisable;
    }

    @Value("${pdf.download.disable:true}")
    public void setPdfDownloadDisable(String pdfDownloadDisable) {
        setPdfDownloadDisableValue(pdfDownloadDisable);
    }

    public static void setPdfDownloadDisableValue(String pdfDownloadDisable) {
        ConfigConstants.pdfDownloadDisable = pdfDownloadDisable;
    }

    @Value("${pdf.bookmark.disable:true}")
    public void setPdfBookmarkDisable(String pdfBookmarkDisable) {
        setPdfBookmarkDisableValue(pdfBookmarkDisable);
    }

    public static void setPdfBookmarkDisableValue(String pdfBookmarkDisable) {
        ConfigConstants.pdfBookmarkDisable = pdfBookmarkDisable;
    }

    @Value("${pdf.disable.editing:true}")
    public void setpdfDisableEditing(String pdfDisableEditing) {
        setPdfDisableEditingValue(pdfDisableEditing);
    }

    public static void setPdfDisableEditingValue(String pdfDisableEditing) {
        ConfigConstants.pdfDisableEditing = pdfDisableEditing;
    }

    @Value("${pdf2jpg.dpi:105}")
    public void pdf2JpgDpi(int pdf2JpgDpi) {
        setPdf2JpgDpiValue(pdf2JpgDpi);
    }

    public static void setPdf2JpgDpiValue(int pdf2JpgDpi) {
        ConfigConstants.pdf2JpgDpi = pdf2JpgDpi;
    }

    @Value("${pdf.timeout:90}")
    public void setPdfTimeout(int pdfTimeout) {
        setPdfTimeoutValue(pdfTimeout);
    }

    public static void setPdfTimeoutValue(int pdfTimeout) {
        ConfigConstants.pdfTimeout = pdfTimeout;
    }

    @Value("${pdf.timeout80:180}")
    public void setPdfTimeout80(int pdfTimeout80) {
        setPdfTimeout80Value(pdfTimeout80);
    }

    public static void setPdfTimeout80Value(int pdfTimeout80) {
        ConfigConstants.pdfTimeout80 = pdfTimeout80;
    }

    @Value("${pdf.timeout200:300}")
    public void setPdfTimeout200(int pdfTimeout200) {
        setPdfTimeout200Value(pdfTimeout200);
    }

    public static void setPdfTimeout200Value(int pdfTimeout200) {
        ConfigConstants.pdfTimeout200 = pdfTimeout200;
    }

    @Value("${pdf.thread:5}")
    public void setPdfThread(int pdfThread) {
        setPdfThreadValue(pdfThread);
    }

    public static void setPdfThreadValue(int pdfThread) {
        ConfigConstants.pdfThread = pdfThread;
    }

    // 8. CAD配置Setter方法
    @Value("${cad.timeout:90}")
    public void setCadTimeout(String cadTimeout) {
        setCadTimeoutValue(cadTimeout);
    }

    public static void setCadTimeoutValue(String cadTimeout) {
        ConfigConstants.cadTimeout = cadTimeout;
    }

    @Value("${cad.thread:5}")
    public void setCadThread(int cadThread) {
        setCadThreadValue(cadThread);
    }

    public static void setCadThreadValue(int cadThread) {
        ConfigConstants.cadThread = cadThread;
    }

    // 9. 文件操作配置Setter方法
    @Value("${file.upload.disable:true}")
    public void setFileUploadDisable(Boolean fileUploadDisable) {
        setFileUploadDisableValue(fileUploadDisable);
    }

    public static void setFileUploadDisableValue(Boolean fileUploadDisable) {
        ConfigConstants.fileUploadDisable = fileUploadDisable;
    }

    @Value("${spring.servlet.multipart.max-file-size:500MB}")
    public void setSize(String size) {
        setSizeValue(size);
    }

    public static void setSizeValue(String size) {
        ConfigConstants.size = size;
    }

    @Value("${delete.password:123456}")
    public void setPassword(String password) {
        setPasswordValue(password);
    }

    public static void setPasswordValue(String password) {
        ConfigConstants.password = password;
    }

    @Value("${delete.source.file:true}")
    public void setDeleteSourceFile(Boolean deleteSourceFile) {
        setDeleteSourceFileValue(deleteSourceFile);
    }

    public static void setDeleteSourceFileValue(Boolean deleteSourceFile) {
        ConfigConstants.deleteSourceFile = deleteSourceFile;
    }

    @Value("${delete.captcha:false}")
    public void setDeleteCaptcha(Boolean deleteCaptcha) {
        setDeleteCaptchaValue(deleteCaptcha);
    }

    public static void setDeleteCaptchaValue(Boolean deleteCaptcha) {
        ConfigConstants.deleteCaptcha = deleteCaptcha;
    }

    // 10. 首页配置Setter方法
    @Value("${beian:default}")
    public void setBeian(String beian) {
        setBeianValue(beian);
    }

    public static void setBeianValue(String beian) {
        ConfigConstants.beian = beian;
    }

    @Value("${home.pagenumber:1}")
    public void setHomePageNumber(String homePageNumber) {
        setHomePageNumberValue(homePageNumber);
    }

    public static void setHomePageNumberValue(String homePageNumber) {
        ConfigConstants.homePageNumber = homePageNumber;
    }

    @Value("${home.pagination:true}")
    public void setHomePagination(String homePagination) {
        setHomePaginationValue(homePagination);
    }

    public static void setHomePaginationValue(String homePagination) {
        ConfigConstants.homePagination = homePagination;
    }

    @Value("${home.pagesize:15}")
    public void setHomePageSize(String homePageSize) {
        setHomePageSizeValue(homePageSize);
    }

    public static void setHomePageSizeValue(String homePageSize) {
        ConfigConstants.homePageSize = homePageSize;
    }

    @Value("${home.search:1}")
    public void setHomeSearch(String homeSearch) {
        setHomeSearchValue(homeSearch);
    }

    public static void setHomeSearchValue(String homeSearch) {
        ConfigConstants.homeSearch = homeSearch;
    }

    // 11. 权限配置Setter方法
    @Value("${kk.Key:}")
    public void setKey(String key) {
        setKeyValue(key);
    }

    public static void setKeyValue(String key) {
        ConfigConstants.key = key;
    }

    @Value("${kk.Picturespreview:true}")
    public void setPicturesPreview(String picturesPreview) {
        setPicturesPreviewValue(Boolean.parseBoolean(picturesPreview));
    }

    public static void setPicturesPreviewValue(boolean picturesPreview) {
        ConfigConstants.picturesPreview = picturesPreview;
    }

    @Value("${kk.Getcorsfile:true}")
    public void setGetCorsFile(String getCorsFile) {
        setGetCorsFileValue(Boolean.parseBoolean(getCorsFile));
    }

    public static void setGetCorsFileValue(boolean getCorsFile) {
        ConfigConstants.getCorsFile = getCorsFile;
    }

    @Value("${kk.addTask:true}")
    public void setAddTask(String addTask) {
        setAddTaskValue(Boolean.parseBoolean(addTask));
    }

    public static void setAddTaskValue(boolean addTask) {
        ConfigConstants.addTask = addTask;
    }

    @Value("${ase.key:1234567890123456}")
    public void setaesKey(String aesKey) {
        setaesKeyValue(aesKey);
    }

    public static void setaesKeyValue(String aesKey) {
        ConfigConstants.aesKey = aesKey;
    }

    // 12. UserAgent配置Setter方法
    @Value("${useragent:false}")
    public void setUserAgent(String userAgent) {
        setUserAgentValue(userAgent);
    }

    public static void setUserAgentValue(String userAgent) {
        ConfigConstants.userAgent = userAgent;
    }

    // 13. Basic认证配置Setter方法
    @Value("${basic.name:}")
    public void setBasicName(String basicName) {
        setBasicNameValue(basicName);
    }

    public static void setBasicNameValue(String basicName) {
        ConfigConstants.basicName = basicName;
    }
}