package kr.hhplus.be.server.application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import kr.hhplus.be.server.application.interceptor.QueueValidationInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final QueueValidationInterceptor queueValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.queueValidationInterceptor)
                .addPathPatterns("/api/**");
    }
}
