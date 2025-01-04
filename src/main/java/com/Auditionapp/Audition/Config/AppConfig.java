package com.Auditionapp.Audition.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

    @Configuration
    @ComponentScan(basePackages = {"com.Auditionapp.Audition"})
    public class AppConfig extends WebMvcConfigurationSupport {


        @Autowired
        AppInterceptor appInterceptor;

        @Autowired
        private WebSecurityConfig filter;


        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(appInterceptor).addPathPatterns("/web/*").excludePathPatterns("/static/**");
        }


        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry
                    .addResourceHandler("/**")
                    .addResourceLocations("classpath:/static/")
                    .addResourceLocations("classpath:/uploaded-files/")
                    .addResourceLocations("file:/home3/myauditi/etc/myauditions.us/Images/")
                    .addResourceLocations("file:C:\\Auditioning\\Images\\");

        }



    }
