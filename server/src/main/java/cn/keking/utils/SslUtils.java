package cn.keking.utils;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * @author 高雄
 */
public class SslUtils {

    /**
     * 创建忽略SSL验证的HttpClient（适用于HttpClient 5.6）
     */
    public static CloseableHttpClient createHttpClientIgnoreSsl() throws Exception {
        // 创建自定义的SSL上下文
        SSLContext sslContext = createIgnoreVerifySSL();

        // 使用SSLConnectionSocketFactoryBuilder构建SSL连接工厂
        DefaultClientTlsStrategy tlsStrategy = new DefaultClientTlsStrategy(
                sslContext, NoopHostnameVerifier.INSTANCE);

        // 使用新的PoolingHttpClientConnectionManagerBuilder构建连接管理器
        // 使用连接管理器构建器
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(tlsStrategy)
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(Timeout.ofSeconds(10))
                        .build())
                .build();

        // 配置连接池参数
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);

        // 配置请求参数
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(10))
                .setResponseTimeout(Timeout.ofSeconds(72))
                .setConnectionRequestTimeout(Timeout.ofSeconds(2))
                .setRedirectsEnabled(true)
                .setMaxRedirects(5)
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRedirectStrategy(DefaultRedirectStrategy.INSTANCE)
                .build();
    }

    /**
     * 创建忽略SSL验证的SSLContext
     */
    private static SSLContext createIgnoreVerifySSL() throws Exception {
        // 使用TLSv1.2或TLSv1.3
        SSLContext sc = SSLContext.getInstance("TLSv1.2");

        // 实现一个X509TrustManager，忽略所有证书验证
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                // 信任所有客户端证书
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                // 信任所有服务器证书
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        sc.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());
        return sc;
    }
}