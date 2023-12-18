package com.kerwin.gallery.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ==============================================================================
 * Author:       Kerwin
 * Created:      2023/11/10
 * Description:
 * ==============================================================================
 */
@Slf4j
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Value("${app.upload_file_path}")
    private String uploadDirPath;

    private final ApplicationProperties applicationProperties;

    public WebConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = this.applicationProperties.getCors();
        if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
            log.debug("Registering CORS filter");
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration("/v2/api-docs", config);
            source.registerCorsConfiguration("/*/api/**", config);
            source.registerCorsConfiguration("/services/*/api/**", config);
        }
        return new CorsFilter(source);
    }

    /**
     * 系统添加额外的静态文件路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源的路径必须要以/结尾，否则不生效
        if (!uploadDirPath.endsWith("/")) {
            uploadDirPath += "/";
        }
        registry.addResourceHandler("/api/static/**").addResourceLocations("file:" + this.uploadDirPath);

    }
}
