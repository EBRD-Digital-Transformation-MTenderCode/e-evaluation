package com.procurement.evaluation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.procurement.evaluation.utils.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    WebConfig.class,
    ServiceConfig.class,
    DatabaseConfig.class
})
public class ApplicationConfig {
    @Bean
    public JsonUtil jsonUtil(final ObjectMapper mapper) {
        return new JsonUtil(mapper);
    }
}
