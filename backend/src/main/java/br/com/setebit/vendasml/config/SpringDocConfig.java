package br.com.setebit.vendasml.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração adicional para garantir que o SpringDoc funcione corretamente
 * mesmo com o GlobalExceptionHandler ativo
 */
@Configuration
public class SpringDocConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("mercadolivre-api")
                .pathsToMatch("/api/**")
                .build();
    }
}

