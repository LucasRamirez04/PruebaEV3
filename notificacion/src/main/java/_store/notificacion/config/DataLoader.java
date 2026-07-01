package _store.notificacion.config;

import _store.notificacion.model.Notificacion;
import _store.notificacion.repository.NotificacionRepository;
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
    CommandLineRunner cargarDatosNotificacion(NotificacionRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<Notificacion> notificacionesNuevas = new ArrayList<>();

                // 1. Una notificación fija para pruebas predecibles (ideal para Postman)
                Notificacion fija = new Notificacion();
                fija.setClienteId(1);
                fija.setTipo("PEDIDO");
                fija.setMensaje("Tu pedido #1 fue creado exitosamente. Total: $45000");
                fija.setEstadoNotificacion("ENVIADA");
                fija.setFechaEnvio(LocalDate.now());
                notificacionesNuevas.add(fija);

                // 2. Inicializamos Datafaker en español
                Faker faker = new Faker(new Locale("es"));
                String[] tipos = {"PEDIDO", "PAGO", "ENVIO"};

                // 3. Generamos 6 notificaciones aleatorias
                for (int i = 0; i < 6; i++) {
                    Notificacion n = new Notificacion();
                    n.setClienteId(faker.number().numberBetween(1, 10));
                    n.setTipo(tipos[faker.number().numberBetween(0, 3)]);
                    n.setMensaje(faker.lorem().sentence(8));
                    n.setEstadoNotificacion("ENVIADA");
                    n.setFechaEnvio(LocalDate.now());
                    notificacionesNuevas.add(n);
                }

                repository.saveAll(notificacionesNuevas);
            }
        };
    }
}