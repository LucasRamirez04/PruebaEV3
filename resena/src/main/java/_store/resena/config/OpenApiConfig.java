package _store.resena.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI resenaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("404Store - API Reseña")
                        .description("Microservicio de reseñas de productos. Valida contra los microservicios "
                                + "Cliente y Producto antes de registrar una reseña, e impide que un mismo "
                                + "cliente reseñe dos veces el mismo producto.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Equipo 404Store")
                                .email("contacto@404store.cl")));
    }
}
