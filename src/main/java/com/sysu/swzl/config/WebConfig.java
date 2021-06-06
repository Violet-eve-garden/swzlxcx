package com.sysu.swzl.config;

import com.sysu.swzl.constant.FileConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 49367
 * @date 2021/5/30 14:12
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/img/upload/**")
                .addResourceLocations("file:"+ FileConstant.UPLOAD_PATH +"/");
    }
}
