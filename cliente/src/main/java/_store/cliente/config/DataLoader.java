package _store.cliente.config;

import _store.cliente.model.Cliente;
import _store.cliente.repository.ClienteRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner cargarDatos(ClienteRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<Cliente> clientesNuevos = new ArrayList<>();

                // 1. Un cliente fijo para pruebas predecibles (ideal para Postman y Testing)
                Cliente clienteFijo = new Cliente();
                clienteFijo.setRun("11111111-1");
                clienteFijo.setNombre("Juan");
                clienteFijo.setApellido("Perez");
                clienteFijo.setCorreo("juan.perez@gmail.com");
                clienteFijo.setTelefono("+56911111111");
                clienteFijo.setDireccion("Av. Siempre Viva 742");
                clientesNuevos.add(clienteFijo);

                // 2. Inicializamos DataFaker
                Faker faker = new Faker(new Locale("es"));

                // 3. Generamos 4 clientes aleatorios con Faker
                for (int i = 0; i < 4; i++) {
                    Cliente cliente = new Cliente();
                    cliente.setRun(faker.number().digits(8) + "-" + faker.number().digit());
                    cliente.setNombre(faker.name().firstName());
                    cliente.setApellido(faker.name().lastName());
                    cliente.setCorreo(faker.internet().emailAddress());
                    cliente.setTelefono("+569" + faker.number().digits(8));
                    cliente.setDireccion(faker.address().fullAddress());
                    clientesNuevos.add(cliente);
                }

                // 4. Guardamos todos los clientes en lote en la BD H2
                repository.saveAll(clientesNuevos);

                System.out.println("====== ¡Datos iniciales de Clientes cargados con éxito! ======");
            }
        };
    }
}
