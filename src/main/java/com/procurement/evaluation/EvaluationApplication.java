package com.procurement.evaluation;

import com.procurement.evaluation.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackageClasses = ApplicationConfig.class
)
public class EvaluationApplication {

    public static void main(final String[] args) {
        SpringApplication.run(EvaluationApplication.class, args);
    }
}
