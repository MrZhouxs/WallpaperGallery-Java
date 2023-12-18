package com.kerwin.gallery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Properties specific to Testapp 1.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private final CorsConfiguration cors = new CorsConfiguration();
    private Integer httpCacheTimeToLiveInDays = 1461;

    public CorsConfiguration getCors() {
        return cors;
    }

    public void setHttpCacheTimeToLiveInDays(Integer httpCacheTimeToLiveInDays) {
        this.httpCacheTimeToLiveInDays = httpCacheTimeToLiveInDays;
    }

    public Integer getHttpCacheTimeToLiveInDays() {
        return httpCacheTimeToLiveInDays;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "cors=" + cors +
                ", httpCacheTimeToLiveInDays=" + httpCacheTimeToLiveInDays +
                '}';
    }
}
