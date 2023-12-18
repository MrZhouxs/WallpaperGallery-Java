package com.kerwin.gallery.config;

import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.ApiParam;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.AlternateTypeBuilder;
import springfox.documentation.builders.AlternateTypePropertyBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * Pageable默认参数在Swagger中并不友好，自定义设置
 */
@Configuration
@SuppressWarnings("SpringJavaAutowritingInspection")
public class SwaggerPageDataConfiguration {

    @Bean
    public AlternateTypeRuleConvention springDataWebPropertiesConvention(final SpringDataWebProperties webProperties) {
        return new AlternateTypeRuleConvention() {
            @Override
            public int getOrder() { return Ordered.HIGHEST_PRECEDENCE; }

            @Override
            public List<AlternateTypeRule> rules() {
                return singletonList(
                        newRule(Pageable.class, pageableDocumentedType(webProperties.getPageable(), webProperties.getSort()))
                );
            }
        };
    }

    private Type pageableDocumentedType(SpringDataWebProperties.Pageable pageable, SpringDataWebProperties.Sort sort) {
        final String firstPage = pageable.isOneIndexedParameters() ? "1" : "0";
        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(fullyQualifiedName(Pageable.class))
                .property(property(pageable.getPageParameter(), Integer.class, ImmutableMap.of(
                        "value", "第page页，从0开始计数," + String.format("允许范围[%s, %s]", firstPage, Integer.MAX_VALUE),
                        "defaultValue", "0",
                        "allowableValues", String.format("range[%s, %s]", firstPage, Integer.MAX_VALUE),
                        "example", "0",
                        "required", true
                )))
                .property(property(pageable.getSizeParameter(), Integer.class, ImmutableMap.of(
                        "value", "每页大小，" + String.format("允许范围[1, %s]", pageable.getMaxPageSize()),
                        "defaultValue", String.valueOf(pageable.getDefaultPageSize()),
                        "allowableValues", String.format("range[1, %s]", pageable.getMaxPageSize()),
                        "example", "20",
                        "required", true
                )))
                .property(property(sort.getSortParameter(), String[].class, ImmutableMap.of(
                        "value", "页面排序,格式为: 字段名,(asc|desc),asc表升序，desc表降序;\nExample: /api/all?size=10&page=0&sort=id,desc&sort=name,asc"
                )))
                .build();
    }

    private String fullyQualifiedName(Class<?> convertedClass) {
        return String.format("%s.generated.%s", convertedClass.getPackage().getName(), convertedClass.getSimpleName());
    }

    private AlternateTypePropertyBuilder property(String name, Class<?> type, Map<String, Object> parameters) {
        return new AlternateTypePropertyBuilder()
                .withName(name)
                .withType(type)
                .withCanRead(true)
                .withCanWrite(true)
                .withAnnotations(Collections.singletonList(AnnotationProxy.of(ApiParam.class, parameters)));
    }
}


@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
class AnnotationProxy implements Annotation, InvocationHandler {
    @Getter
    private final Class<? extends Annotation> annotationType;
    private final Map<String, Object> values;

    public static <A extends Annotation> A of(Class<A> annotation, Map<String, Object> values) {
        return (A) Proxy.newProxyInstance(annotation.getClassLoader(),
                new Class[]{annotation},
                new AnnotationProxy(annotation, new HashMap<String, Object>(values) {{
                    put("annotationType", annotation); // Required because getDefaultValue() returns null for this call
                }}));
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        return values.getOrDefault(method.getName(), method.getDefaultValue());
    }
}
