package cn.keking.web.filter;

import cn.keking.config.ConfigConstants;
import cn.keking.utils.WebUtils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

/**
 * @author chenjh
 * @since 2020/2/18 19:13
 */
public class TrustHostFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TrustHostFilter.class);
    private String notTrustHostHtmlView;

    @Override
    public void init(FilterConfig filterConfig) {
        ClassPathResource classPathResource = new ClassPathResource("web/notTrustHost.html");
        try {
            classPathResource.getInputStream();
            byte[] bytes = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
            this.notTrustHostHtmlView = new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to load notTrustHost.html file", e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String url = WebUtils.getSourceUrl(request);
        String host = WebUtils.getHost(url);
        assert host != null;
        if (isNotTrustHost(host)) {
            String html = this.notTrustHostHtmlView.replace("${current_host}", host);
            response.getWriter().write(html);
            response.getWriter().close();
        } else {
            chain.doFilter(request, response);
        }
    }

    public boolean isNotTrustHost(String host) {
        // 如果配置了黑名单，优先检查黑名单
        if (CollectionUtils.isNotEmpty(ConfigConstants.getNotTrustHostSet())) {
            return matchAnyPattern(host, ConfigConstants.getNotTrustHostSet());
        }

        // 如果配置了白名单，检查是否在白名单中
        if (CollectionUtils.isNotEmpty(ConfigConstants.getTrustHostSet())) {
            // 支持通配符 * 表示允许所有主机
            if (ConfigConstants.getTrustHostSet().contains("*")) {
                logger.debug("允许所有主机访问（通配符模式）: {}", host);
                return false;
            }
            return !matchAnyPattern(host, ConfigConstants.getTrustHostSet());
        }

        // 安全加固：默认拒绝所有未配置的主机（防止SSRF攻击）
        // 如果需要允许所有主机，请在配置文件中明确设置 trust.host = *
        logger.warn("未配置信任主机列表，拒绝访问主机: {}，请在配置文件中设置 trust.host 或 KK_TRUST_HOST 环境变量", host);
        return true;
    }

    private boolean matchAnyPattern(String host, Set<String> hostPatterns) {
        String normalizedHost = host == null ? "" : host.toLowerCase();
        for (String hostPattern : hostPatterns) {
            if (matchHostPattern(normalizedHost, hostPattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 支持三种匹配方式：
     * 1. 精确匹配：example.com
     * 2. 通配符匹配：*.example.com、192.168.*
     * 3. IPv4 CIDR：192.168.0.0/16
     */
    private boolean matchHostPattern(String host, String hostPattern) {
        if (hostPattern == null || hostPattern.trim().isEmpty()) {
            return false;
        }
        String pattern = hostPattern.trim().toLowerCase();

        if ("*".equals(pattern)) {
            return true;
        }

        if (pattern.contains("/")) {
            return matchIpv4Cidr(host, pattern);
        }

        if (pattern.contains("*")) {
            String regex = wildcardToRegex(pattern);
            return host.matches(regex);
        }

        return host.equals(pattern);
    }

    private String wildcardToRegex(String wildcard) {
        StringBuilder regexBuilder = new StringBuilder("^");
        String[] parts = wildcard.split("\\*", -1);
        for (int i = 0; i < parts.length; i++) {
            regexBuilder.append(Pattern.quote(parts[i]));
            if (i < parts.length - 1) {
                regexBuilder.append(".*");
            }
        }
        regexBuilder.append("$");
        return regexBuilder.toString();
    }

    private boolean matchIpv4Cidr(String host, String cidr) {
        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }
            InetAddress hostAddress = InetAddress.getByName(host);
            InetAddress networkAddress = InetAddress.getByName(parts[0]);
            int prefixLength = Integer.parseInt(parts[1]);

            if (!(hostAddress instanceof Inet4Address) || !(networkAddress instanceof Inet4Address) || prefixLength < 0 || prefixLength > 32) {
                return false;
            }

            int mask = prefixLength == 0 ? 0 : -1 << (32 - prefixLength);
            int hostInt = inet4ToInt(hostAddress);
            int networkInt = inet4ToInt(networkAddress);
            return (hostInt & mask) == (networkInt & mask);
        } catch (UnknownHostException | NumberFormatException e) {
            return false;
        }
    }

    private int inet4ToInt(InetAddress address) {
        byte[] bytes = address.getAddress();
        return ((bytes[0] & 0xFF) << 24)
                | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8)
                | (bytes[3] & 0xFF);
    }

    @Override
    public void destroy() {

    }

}
