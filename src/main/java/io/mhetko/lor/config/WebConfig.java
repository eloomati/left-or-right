package io.mhetko.lor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:avatars/");
    }

    @Bean
    public WebClient openAiWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://api.openai.com/v1").build();
    }

    @Bean
    public WebClient huggingFaceWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://api-inference.huggingface.co").build();
    }

    @Bean
    public WebClient ollamaWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://localhost:11434").build();
    }
}
