package cn.keking.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 配置文件下载控制器
 */
@Profile("dev")
@RestController
public class ConfigController {

    private final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    /**
     * 下载 application.properties 配置文件
     * @return 配置文件内容
     */
    @GetMapping("/config/download")
    public ResponseEntity<Resource> downloadConfig() {
        try {
            // 尝试多个可能的配置文件位置
            String[] possiblePaths = {
                "config/application.properties",                     // 相对路径（当前工作目录）
                "/opt/kkFileView-5.0.0/config/application.properties", // 容器内默认路径
                "server/src/main/config/application.properties",     // 源码开发路径
                "application.properties"                             // 当前目录
            };
            
            File configFile = null;
            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    configFile = file;
                    logger.info("找到配置文件: {}", file.getAbsolutePath());
                    break;
                }
            }
            
            if (configFile == null) {
                logger.warn("未找到配置文件，尝试从类路径加载默认配置");
                // 尝试从类路径加载默认配置文件
                Resource resource = new org.springframework.core.io.ClassPathResource("config/application.properties");
                if (resource.exists()) {
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"application.properties\"")
                            .contentType(MediaType.TEXT_PLAIN)
                            .body(resource);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
            
            Path path = Paths.get(configFile.getAbsolutePath());
            Resource resource = new FileSystemResource(path);
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"application.properties\"");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");
            
            // 尝试确定内容类型
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = MediaType.TEXT_PLAIN_VALUE;
            }
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(configFile.length())
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
            
        } catch (IOException e) {
            logger.error("下载配置文件时发生错误", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}