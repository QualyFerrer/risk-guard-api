package com.cesar.riskguard.configuration;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                .title("Riskguard API")
                .description("API de detectação de fraude em transações financeiras. "
                       + "Analisa transações em tempo real com score de risco ponderado.")
                .version("1.0.0")
                .contact(new Contact()
                        .name("César Ferrer")
                        .url("https://github.com/QualyFerrer/risk-guard-api")));
    }
}
