package br.com.prioris.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI priorisOpenAPI() {

        Server servidorLocal = new Server()
                .url("http://localhost:8080")
                .description("Servidor local de desenvolvimento");

        Contact contato = new Contact()
                .name("Projeto Prioris");

        Info info = new Info()
                .title("Prioris API")
                .version("1.0.0")
                .description(
                        "API REST do Prioris, sistema de produtividade "
                                + "baseado em priorização, ciclos de 12 semanas, "
                                + "planejamento semanal, foco e acompanhamento "
                                + "da execução."
                )
                .contact(contato)
                .license(
                        new License()
                                .name("Projeto acadêmico")
                );

        return new OpenAPI()
                .info(info)
                .servers(List.of(servidorLocal));
    }
}