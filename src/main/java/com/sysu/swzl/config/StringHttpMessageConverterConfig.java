package com.sysu.swzl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MimeType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author 49367
 * @date 2021/6/22 13:32
 */
@Configuration
public class StringHttpMessageConverterConfig {
    @Bean
    public StringHttpMessageConverter getMyStringHttpMessageConverter(){
        StringHttpMessageConverter converter = new StringHttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(
                // MediaType(String type, String subtype, Charset charset)
                // application/json;charset=utf-8 的 type 是 application， subtype 是 json， charset是utf-8
                new MediaType("application", "json", StandardCharsets.UTF_8),
                new MediaType("text", "html", StandardCharsets.UTF_8),
                MediaType.TEXT_PLAIN,
                MediaType.ALL
        ));

        return converter;
    }
}
