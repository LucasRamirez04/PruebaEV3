package _store.devolucion.config;

import _store.devolucion.model.Devolucion;
import _store.devolucion.repository.DevolucionRepository;
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
    CommandLineRunner cargarDatos(DevolucionRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<Devolucion> devolucionesNuevas = new ArrayList<>();

                // 1. Una devolucion fija para pruebas predecibles (ideal para Postman y Testing)
                Devolucion devolucionFija = new Devolucion();
                devolucionFija.setPedidoId(1);
                devolucionFija.setMotivo("Producto llego con la caja dañada");
                devolucionFija.setMontoDevuelto(45000.0);
                devolucionFija.setEstado("PENDIENTE");
                devolucionesNuevas.add(devolucionFija);

                // 2. Inicializamos DataFaker
                Faker faker = new Faker(new Locale("es"));

                String[] estados = {"PENDIENTE", "ACEPTADA", "RECHAZADA"};

                // 3. Generamos 4 devoluciones aleatorias con Faker
                for (int i = 0; i < 4; i++) {
                    Devolucion devolucion = new Devolucion();
                    devolucion.setPedidoId(faker.number().numberBetween(1, 20));
                    devolucion.setMotivo(faker.commerce().productName() + " llego en mal estado");
                    devolucion.setMontoDevuelto(faker.number().randomDouble(2, 5000, 80000));

                    String estado = estados[faker.number().numberBetween(0, estados.length)];
                    devolucion.setEstado(estado);
                    if (!"PENDIENTE".equals(estado)) {
                        devolucion.setFechaResolucion(LocalDate.now());
                    }

                    devolucionesNuevas.add(devolucion);
                }

                // 4. Guardamos todas las devoluciones en lote en la BD H2
                repository.saveAll(devolucionesNuevas);

                System.out.println("====== ¡Datos iniciales de Devoluciones cargados con éxito! ======");
            }
        };
    }
}
