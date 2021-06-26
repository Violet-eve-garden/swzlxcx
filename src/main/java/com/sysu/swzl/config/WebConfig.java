package com.sysu.swzl.config;

import com.sysu.swzl.constant.FileConstant;
import com.sysu.swzl.interceptor.PushInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author 49367
 * @date 2021/5/30 14:12
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private PushInterceptor pushInterceptor;

    @Autowired
    private StringHttpMessageConverter stringHttpMessageConverter;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/img/upload/**")
                .addResourceLocations("file:"+ FileConstant.UPLOAD_PATH +"/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pushInterceptor).addPathPatterns("/push/**");
    }

//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        log.info(stringHttpMessageConverter.getSupportedMediaTypes().toString());
//        converters.add(stringHttpMessageConverter);
//    }
}
