package com.kerwin.gallery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * ==============================================================================
 * Author:       Kerwin
 * Created:      2023/6/9
 * Description:  RestTemplate创建Bean
 * ==============================================================================
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> httpMessageConverterList = restTemplate.getMessageConverters();
        httpMessageConverterList.forEach(httpMessageConverter -> {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) httpMessageConverter;
                messageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        });
        restTemplate.getMessageConverters().add(new MyMappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    public static class MyMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        public MyMappingJackson2HttpMessageConverter() {
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.ALL);
            setSupportedMediaTypes(mediaTypes);
        }
    }
}
