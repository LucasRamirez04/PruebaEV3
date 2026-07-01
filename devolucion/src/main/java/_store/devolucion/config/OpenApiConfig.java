package _store.devolucion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI devolucionOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("404Store - Microservicio Devolucion")
                .description("Gestion de solicitudes de devolucion de pedidos")
                .version("1.0.0"));
    }
}
