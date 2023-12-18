package com.kerwin.gallery.config;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String VERSION = "1.0";

    @Value("${app.swagger:false}")
    private Boolean swaggerEnabled;

    @Bean
    public Docket createRestApi() {
        // 设置全局状态码解释
        List<ResponseMessage> responseMessageList = new ArrayList<>();
        responseMessageList.add(new ResponseMessageBuilder().code(200).message("成功").build());
        responseMessageList.add(new ResponseMessageBuilder().code(500).message("代码异常").build());
        return new Docket(DocumentationType.SWAGGER_2)
                .globalResponseMessage(RequestMethod.GET, responseMessageList)
                .globalResponseMessage(RequestMethod.POST, responseMessageList)
                .globalResponseMessage(RequestMethod.PUT, responseMessageList)
                .globalResponseMessage(RequestMethod.DELETE, responseMessageList)
                // 是否启用Swagger2
                .enable(swaggerEnabled)
                // 设置Headers
//                .globalOperationParameters(Lists.newArrayList(
//                        new ParameterBuilder().name("account").description("当前登录的用户账号").modelRef(new ModelRef("string"))
//                                .parameterType("header").required(true).defaultValue("admin").build()
//                ))
//                .groupName("Swagger2Api")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 添加摘要信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("壁纸画廊")
                .contact(new Contact("Kerwin个人空间", "", "2772190040@qq.com"))
                .description("壁纸画廊接口文档")
                .version(VERSION)
                .build();
    }

}