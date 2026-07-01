package _store.notificacion;

import _store.notificacion.exception.NotificacionNoEncontradaException;
import _store.notificacion.model.Notificacion;
import _store.notificacion.repository.NotificacionRepository;
import _store.notificacion.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceTest {

    @Mock
    NotificacionRepository notificacionRepository;

    @InjectMocks
    NotificacionService notificacionService;

    @Test
    void buscarPorId_notificacionExiste_retornaNotificacion() {
        Notificacion n = new Notificacion();
        n.setId(1);
        n.setClienteId(10);
        n.setTipo("PEDIDO");
        n.setMensaje("Pedido creado");

        when(notificacionRepository.findById(1)).thenReturn(Optional.of(n));

        Notificacion resultado = notificacionService.buscarPorId(1);
        assertEquals("PEDIDO", resultado.getTipo());
    }

    @Test
    void buscarPorId_notificacionNoExiste_lanzaExcepcion() {
        when(notificacionRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(NotificacionNoEncontradaException.class, () -> notificacionService.buscarPorId(99));
    }

    @Test
    void crearDesdeEvento_datosValidos_guardaConEstadoEnviada() {
        Map<String, Object> datos = Map.of(
                "clienteId", 5,
                "tipo", "PAGO",
                "mensaje", "Pago confirmado"
        );

        Notificacion guardada = new Notificacion();
        guardada.setClienteId(5);
        guardada.setTipo("PAGO");
        guardada.setMensaje("Pago confirmado");
        guardada.setEstadoNotificacion("ENVIADA");

        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(guardada);

        Notificacion resultado = notificacionService.crearDesdeEvento(datos);
        assertEquals("ENVIADA", resultado.getEstadoNotificacion());
    }
}