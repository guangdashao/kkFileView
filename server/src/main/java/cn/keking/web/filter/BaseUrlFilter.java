package cn.keking.web.filter;

import cn.keking.ServerMain;
import cn.keking.config.ConfigConstants;
import cn.keking.config.UserInfoConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author chenjh
 * @since 2020/5/13 18:27
 */
public class BaseUrlFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(BaseUrlFilter.class);
    private static String BASE_URL;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getBaseUrl() {
        String baseUrl;
        try {
            baseUrl = (String) RequestContextHolder.currentRequestAttributes().getAttribute("baseUrl", 0);
        } catch (Exception e) {
            baseUrl = BASE_URL;
        }
        return baseUrl;
    }


    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String baseUrl;
        String configBaseUrl = ConfigConstants.getBaseUrl();

        final HttpServletRequest servletRequest = (HttpServletRequest) request;
        //1、支持通过 http header 中 X-Base-Url 来动态设置 baseUrl 以支持多个域名/项目的共享使用
        final String urlInHeader = servletRequest.getHeader("X-Base-Url");
        final String currentApp = servletRequest.getHeader(UserInfoConstants.YUJIA_CURRENT_APP_HEADER);
        if (StringUtils.isNotEmpty(urlInHeader)) {
            baseUrl = urlInHeader;
        } else {
            //2、尝试从 x-yujia-current-app 头中解析 uri 字段作为 baseUrl
            String parsedUri = parseUriFromCurrentApp(currentApp);
            if (parsedUri != null) {
                baseUrl = parsedUri;
            } else if (configBaseUrl != null && !ConfigConstants.DEFAULT_VALUE.equalsIgnoreCase(configBaseUrl)) {
                //3、如果配置文件中配置了 baseUrl 且不为 default 则以配置文件为准
                baseUrl = configBaseUrl;
            } else {
                //4、默认动态拼接 baseUrl
                baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                        + servletRequest.getContextPath() + "/";
            }
        }

        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl.concat("/");
        }

        BASE_URL = baseUrl;
        request.setAttribute("baseUrl", baseUrl);
        filterChain.doFilter(request, response);
    }

    private String parseUriFromCurrentApp(String currentApp) {
        if (StringUtils.isEmpty(currentApp)) {
            return null;
        }
        try {
            String decoded = URLDecoder.decode(currentApp, StandardCharsets.UTF_8);
            Map<String, Object> map = objectMapper.readValue(decoded, Map.class);
            Object uriObj = map.get("uri");
            if (uriObj instanceof String) {
                return (String) uriObj;
            }
        } catch (Exception e) {
            logger.error("解析header 中的 当前应用信息失败", e);
        }
        return null;
    }



    @Override
    public void destroy() {

    }
}
