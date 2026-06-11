package com.recipe.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局 CORS 跨域配置
 * <p>
 * 允许前端（localhost:5173）跨域访问后端 API。
 * 与控制器上的 {@code @CrossOrigin} 注解叠加生效，
 * 覆盖没有注解的端点以及 OPTIONS 预检请求。
 * <p>
 * <b>生产环境注意：</b>应将 allowedOrigins 改为具体域名。
 */
@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                          // 对所有路径生效
                .allowedOriginPatterns("*")                 // 开发环境允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")                        // 允许所有请求头
                .allowCredentials(true)                     // 允许携带 Cookie
                .maxAge(3600);                              // 预检请求缓存 1 小时

        log.info("CORS 全局配置已生效：允许所有来源跨域访问");
    }

}
