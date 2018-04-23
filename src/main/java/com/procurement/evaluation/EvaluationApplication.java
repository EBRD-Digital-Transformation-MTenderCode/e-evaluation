package com.procurement.evaluation;

import com.procurement.evaluation.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(scanBasePackageClasses = ApplicationConfig.class)
@EnableEurekaClient
public class EvaluationApplication {

    public static void main(final String[] args) {
        SpringApplication.run(EvaluationApplication.class, args);
    }
}
