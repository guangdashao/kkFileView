package cn.keking.web.filter;

import cn.keking.config.ConfigConstants;
import cn.keking.config.UserInfoConstants;
import cn.keking.config.WatermarkConfigConstants;
import cn.keking.utils.KkFileUtils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author chenjh
 * @since 2020/5/13 18:34
 */
public class AttributeSetFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        this.setWatermarkAttribute(request);
        this.setFileAttribute(request);
        filterChain.doFilter(request, response);
    }

    /**
     * 设置办公文具预览逻辑需要的属性
     * @param request request
     */
    private void setFileAttribute(ServletRequest request){
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        request.setAttribute("pdfPresentationModeDisable", ConfigConstants.getPdfPresentationModeDisable());
        request.setAttribute("pdfOpenFileDisable", ConfigConstants.getPdfOpenFileDisable());
        request.setAttribute("pdfPrintDisable", ConfigConstants.getPdfPrintDisable());
        request.setAttribute("pdfDownloadDisable", ConfigConstants.getPdfDownloadDisable());
        request.setAttribute("pdfBookmarkDisable", ConfigConstants.getPdfBookmarkDisable());
        request.setAttribute("pdfDisableEditing", ConfigConstants.getPdfDisableEditing());
        request.setAttribute("switchDisabled", ConfigConstants.getOfficePreviewSwitchDisabled());
        request.setAttribute("fileUploadDisable", ConfigConstants.getFileUploadDisable());
        request.setAttribute("beian", ConfigConstants.getBeian());
        request.setAttribute("size", ConfigConstants.maxSize());
        request.setAttribute("deleteCaptcha", ConfigConstants.getDeleteCaptcha());
        request.setAttribute("homePageNumber", ConfigConstants.getHomePageNumber());
        request.setAttribute("homePagination", ConfigConstants.getHomePagination());
        request.setAttribute("homePageSize", ConfigConstants.getHomePageSize());
        request.setAttribute("homeSearch", ConfigConstants.getHomeSearch());
        request.setAttribute("isshowaeskey", ConfigConstants.getisShowaesKey());
        request.setAttribute("isjavascript", ConfigConstants.getisJavaScript());
        request.setAttribute("xlsxallowEdit", ConfigConstants.getxlsxAllowEdit());
        request.setAttribute("xlsxshowtoolbar", ConfigConstants.getxlsxShowtoolbar());
        request.setAttribute("aeskey", ConfigConstants.getaesKey());
        request.setAttribute("isshowkey", ConfigConstants.getisShowKey());
        request.setAttribute("kkkey", ConfigConstants.getKey());
        request.setAttribute("scriptjs", ConfigConstants.getscriptJs());
    }

    /**
     * 设置水印属性
     * @param request request
     */

    private void setWatermarkAttribute(ServletRequest request) {
        String watermarkTxttmp= KkFileUtils.htmlEscape(request.getParameter("watermarkTxt"));
//        request.setAttribute("watermarkTxt", watermarkTxt != null ? watermarkTxt : WatermarkConfigConstants.getWatermarkTxt());
        // shaogd 2026/1/7 水印属性,不从 请求参数中取
        // 从header 中获取，属性为YUJIA_PRINCIPAL_HEADER
        String watermarkTxt1 = watermarkTxttmp != null ? watermarkTxttmp : WatermarkConfigConstants.getWatermarkTxt();
        String watermarkTxt=null;
        if(StringUtils.hasText(watermarkTxt1)) {
            // watermarkTxt1 不为空，表示启用水印功能 UserInfoFilter中设置
            watermarkTxt = (String)request.getAttribute(UserInfoConstants.YUJIA_PRINCIPAL_ATTRIBUTE);

            watermarkTxt = KkFileUtils.htmlEscape(watermarkTxt);
        }
        // 如果开启水印功能， 如果从header中获取到用户信息，和将 配置中的水印信息与用户信息进行拼接
        request.setAttribute("watermarkTxt", watermarkTxt != null ? watermarkTxt1+":"+watermarkTxt : watermarkTxt1);


        String watermarkXSpace =  KkFileUtils.htmlEscape(request.getParameter("watermarkXSpace"));
        if (!KkFileUtils.isInteger(watermarkXSpace)){
            watermarkXSpace =null;
        }
        request.setAttribute("watermarkXSpace", watermarkXSpace != null ? watermarkXSpace : WatermarkConfigConstants.getWatermarkXSpace());
        String watermarkYSpace =  KkFileUtils.htmlEscape(request.getParameter("watermarkYSpace"));
       if (!KkFileUtils.isInteger(watermarkYSpace)){
           watermarkYSpace =null;
       }
        request.setAttribute("watermarkYSpace", watermarkYSpace != null ? watermarkYSpace : WatermarkConfigConstants.getWatermarkYSpace());
        String watermarkFont =  KkFileUtils.htmlEscape(request.getParameter("watermarkFont"));
        request.setAttribute("watermarkFont", watermarkFont != null ? watermarkFont : WatermarkConfigConstants.getWatermarkFont());
        String watermarkFontsize =  KkFileUtils.htmlEscape(request.getParameter("watermarkFontsize"));
        request.setAttribute("watermarkFontsize", watermarkFontsize != null ? watermarkFontsize : WatermarkConfigConstants.getWatermarkFontsize());
        String watermarkColor =  KkFileUtils.htmlEscape(request.getParameter("watermarkColor"));
        request.setAttribute("watermarkColor", watermarkColor != null ? watermarkColor : WatermarkConfigConstants.getWatermarkColor());
        String watermarkAlpha =  KkFileUtils.htmlEscape(request.getParameter("watermarkAlpha"));
        if (!KkFileUtils.isInteger(watermarkAlpha)){
            watermarkAlpha =null;
        }
        request.setAttribute("watermarkAlpha", watermarkAlpha != null ? watermarkAlpha : WatermarkConfigConstants.getWatermarkAlpha());
        String watermarkWidth = KkFileUtils.htmlEscape(request.getParameter("watermarkWidth"));
        if (!KkFileUtils.isInteger(watermarkWidth)){
            watermarkWidth =null;
        }
        request.setAttribute("watermarkWidth", watermarkWidth != null ? watermarkWidth : WatermarkConfigConstants.getWatermarkWidth());
        String watermarkHeight = KkFileUtils.htmlEscape(request.getParameter("watermarkHeight"));
        if (!KkFileUtils.isInteger(watermarkHeight)){
            watermarkHeight =null;
        }
        request.setAttribute("watermarkHeight", watermarkHeight != null ? watermarkHeight : WatermarkConfigConstants.getWatermarkHeight());
        String watermarkAngle = KkFileUtils.htmlEscape(request.getParameter("watermarkAngle"));
        if (!KkFileUtils.isInteger(watermarkAngle)){
            watermarkAngle =null;
        }
        request.setAttribute("watermarkAngle", watermarkAngle != null ? watermarkAngle : WatermarkConfigConstants.getWatermarkAngle());
    }

    @Override
    public void destroy() {

    }
}
