package org.httptest.networkclient.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 정적 리소스 설정 커스터마이징 - WebConfigurer
 * WebMvcConfigurer를 구현하는 클래스에서 addResourceHandlers를 override하여 정적 리소스 핸들러를 커스터마이징할 수 있다.
 *
 * 이 방법을 사용하면 스프링 부트가 제공하는 정적 리소스 핸들러는 그대로 사용하면서 커스텀 핸들러가 추가된다.
 *
 * @Configuration
 * public class WebConfig implements WebMvcConfigurer {
 *
 *     @Override
 *     public void addResourceHandlers(ResourceHandlerRegistry registry) {
 *         registry.addResourceHandler("/m/**")
 *                 .addResourceLocations("classpath:/m/")
 *                 .setCachePeriod(20)
 *                 ;
 *     }
 * }
 *
 * /m/** 패턴 요청 시 classpath의 /m/ 디렉토리에서 정적 리소스를 찾아 응답하도록 하는 설정이다.
 *
 * setCachePeriod를 통해 캐싱 전략을 설정해주어야 한다. (초 단위)
 */
@Configuration
public class ResourceConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("file:///" + System.getProperty("lee.home") + File.separator + "images/");
        registry.addResourceHandler("/samples/**").addResourceLocations("file:///" + System.getProperty("lee.home") + File.separator + "samples/");
        registry.addResourceHandler("/fonts/**").addResourceLocations("file:///" + System.getProperty("lee.home") + File.separator + "fonts/");
        registry.addResourceHandler("/WEB-INF/**").addResourceLocations("/WEB-INF/**");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/"); // http://localhost:8080/favicon.ico 로 요청 시 resources/favicon.ico 파일 접근 가능
    }
}