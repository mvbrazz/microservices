package com.projeto.microservices.configuracoes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class ConfiguracaoSwagger {

    @Bean
    public OpenAPI OpenApi(){
        return new OpenAPI()
        .info(new Info()
            .title("Documentação e testes das Rotas Api")
            .version("v1")
            .description("Microservices - Swagger")
        );
    }
    
}
