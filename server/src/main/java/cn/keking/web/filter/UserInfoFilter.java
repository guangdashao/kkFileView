package cn.keking.web.filter;

import cn.keking.config.UserInfoConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 用于鉴权判断，必须是网关过来的请求，有header 才可以继续访问
 */

public class UserInfoFilter implements Filter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        this.setWatermarkAttribute(request);
//        this.setFileAttribute(request);
        // 获取
        HttpServletResponse servletResponse1 = (HttpServletResponse) servletResponse;
        if (servletRequest instanceof HttpServletRequest request1) {
            String principal = request1.getHeader(UserInfoConstants.YUJIA_PRINCIPAL_HEADER);
            if (principal != null) {
                principal = URLDecoder.decode(principal, StandardCharsets.UTF_8);
                try {
                    var principaleMap = objectMapper.readValue(principal, Map.class);
                    request1.setAttribute(UserInfoConstants.YUJIA_PRINCIPAL_ATTRIBUTE, principaleMap.get("username").toString() + "(" + principaleMap.get("name") + ")");
                } catch (JsonProcessingException e) {
                    servletResponse1.setContentType("text/html");
                    servletResponse1.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    servletResponse1.getWriter().write("非法用户");
                    return;
                }
            } else {
                servletResponse1.setContentType("text/html");
                servletResponse1.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                servletResponse1.getWriter().write("非法用户");
                return;
            }
        } else {
            servletResponse1.setContentType("text/html");
            servletResponse1.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            servletResponse1.getWriter().write("非法请求");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
