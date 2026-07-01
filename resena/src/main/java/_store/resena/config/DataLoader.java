package _store.resena.config;

import _store.resena.model.Resena;
import _store.resena.repository.ResenaRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner cargarDatos(ResenaRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<Resena> resenasNuevas = new ArrayList<>();

                // 1. Reseña fija: predecible para Postman y defensa técnica
                resenasNuevas.add(Resena.builder()
                        .clienteId(1)
                        .productoId(1)
                        .calificacion(5)
                        .comentario("Excelente producto, llegó antes de lo esperado y en perfecto estado.")
                        .fechaResena(LocalDate.now())
                        .build());

                // 2. Segunda reseña fija: diferente cliente para el mismo producto
                resenasNuevas.add(Resena.builder()
                        .clienteId(2)
                        .productoId(1)
                        .calificacion(4)
                        .comentario("Muy buena calidad, aunque el empaque podría mejorar.")
                        .fechaResena(LocalDate.now().minusDays(2))
                        .build());

                // 3. Reseña de otro producto para mostrar el promedio en el endpoint
                resenasNuevas.add(Resena.builder()
                        .clienteId(1)
                        .productoId(2)
                        .calificacion(3)
                        .comentario("Producto aceptable pero no superó mis expectativas.")
                        .fechaResena(LocalDate.now().minusDays(5))
                        .build());

                // 4. Datos aleatorios con DataFaker
                // Usamos clienteIds 3-10 y productoIds 3-20 para no colisionar con los fijos
                Faker faker = new Faker(Locale.of("es"));

                for (int i = 0; i < 4; i++) {
                    int calificacion = faker.number().numberBetween(1, 6); // 1 a 5

                    resenasNuevas.add(Resena.builder()
                            .clienteId(i + 3) // 3, 4, 5, 6
                            .productoId(faker.number().numberBetween(3, 20))
                            .calificacion(calificacion)
                            .comentario(calificacion >= 4
                                    ? faker.lorem().sentence(8)
                                    : "Podría mejorar. " + faker.lorem().sentence(5))
                            .fechaResena(LocalDate.now().minusDays(faker.number().numberBetween(1, 30)))
                            .build());
                }

                repository.saveAll(resenasNuevas);
                System.out.println("====== ¡Datos iniciales de Reseñas cargados con éxito! ======");
            }
        };
    }
}
