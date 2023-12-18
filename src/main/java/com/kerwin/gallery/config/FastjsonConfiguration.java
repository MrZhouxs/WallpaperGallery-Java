//package com.kerwin.gallery.config;
//
//import com.alibaba.fastjson.PropertyNamingStrategy;
//import com.alibaba.fastjson.serializer.SerializeConfig;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.alibaba.fastjson.support.config.FastJsonConfig;
//import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import springfox.documentation.spring.web.json.Json;
//
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * ==============================================================================
// * Author:       Kerwin
// * Created:      2023/11/10
// * Description:
// * ==============================================================================
// */
//@Configuration
//public class FastjsonConfiguration {
//
//    @Bean
//    public FastJsonHttpMessageConverter httpMessageConverter() {
//        // 定义一个convert转换消息的对象
//        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
//
//        //升级最新版本需加=============================================================
//        List<MediaType> supportedMediaTypes = new ArrayList<>();
//        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
//        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
//        supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
//        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
//        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
//        supportedMediaTypes.add(MediaType.APPLICATION_PDF);
//        supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
//        supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
//        supportedMediaTypes.add(MediaType.APPLICATION_XML);
//        supportedMediaTypes.add(MediaType.IMAGE_GIF);
//        supportedMediaTypes.add(MediaType.IMAGE_JPEG);
//        supportedMediaTypes.add(MediaType.IMAGE_PNG);
//        supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
//        supportedMediaTypes.add(MediaType.TEXT_HTML);
//        supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
//        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
//        supportedMediaTypes.add(MediaType.TEXT_XML);
//        fastJsonHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
//
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//
//        //驼峰或者下划线 格式的配置
//        //SerializeConfig serializeConfig = new SerializeConfig();
//        //serializeConfig.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
//        //fastJsonConfig.setSerializeConfig(serializeConfig);
//        //SerializeConfig.getGlobalInstance().setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
//        SerializeConfig.getGlobalInstance().propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
//
//        fastJsonConfig.setSerializerFeatures(
//                SerializerFeature.PrettyFormat,
//                SerializerFeature.WriteMapNullValue,        // 是否输出值为null的字段,默认为false,我们将它打开
//                SerializerFeature.WriteNullListAsEmpty,     // 将Collection类型字段的字段空值输出为[]
////                SerializerFeature.WriteNullStringAsEmpty,   // 将字符串类型字段的空值输出为空字符串
////                SerializerFeature.WriteNullNumberAsZero,    // 将数值类型字段的空值输出为0
//                SerializerFeature.WriteDateUseDateFormat,
//                SerializerFeature.DisableCircularReferenceDetect    // 禁用循环引用
//        );
//
//        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
//
//        // 设置时间格式
////        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        // 设置默认时区  必须在此设置 其他地方设置无效果
////        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
////        TimeZone.setDefault(TimeZone.getTimeZone("GMT+08:00"));
////        JSON.defaultTimeZone = TimeZone.getTimeZone("GMT+08:00");
//
//        // 添加fastJson配置信息
//        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
//        fastJsonHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
//
//        return fastJsonHttpMessageConverter;
//    }
//
//
//}
