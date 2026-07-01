package _store.devolucion;

import _store.devolucion.dto.DevolucionRequest;
import _store.devolucion.exception.DevolucionNoEncontradaException;
import _store.devolucion.exception.PedidoNoEncontradoException;
import _store.devolucion.model.Devolucion;
import _store.devolucion.repository.DevolucionRepository;
import _store.devolucion.service.DevolucionService;
import _store.devolucion.webclient.PedidoClient;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

// Pruebas unitarias del servicio de Devolucion.
// Se usa Datafaker para generar datos realistas (motivos, montos) y Mockito
// para simular el repositorio JPA y el WebClient hacia Pedido.
@ExtendWith(MockitoExtension.class)
class DevolucionServiceTest {

    @Mock
    private DevolucionRepository devolucionRepository;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private DevolucionService devolucionService;

    private final Faker faker = new Faker();

    private DevolucionRequest request;
    private Integer pedidoIdFake;
    private String motivoFake;

    @BeforeEach
    void setUp() {
        pedidoIdFake = faker.number().numberBetween(1, 1000);
        motivoFake = faker.commerce().productName() + " llego dañado";

        request = new DevolucionRequest();
        request.setPedidoId(pedidoIdFake);
        request.setMotivo(motivoFake);
    }

    @Test
    void solicitar_debeCrearDevolucion_cuandoPedidoExiste() {
        // Given: un pedido valido devuelto por el microservicio Pedido
        Map<String, Object> pedidoSimulado = Map.of("id", pedidoIdFake, "total", 25000.0);
        when(pedidoClient.obtenerPedidoId(pedidoIdFake)).thenReturn(pedidoSimulado);
        when(devolucionRepository.save(any(Devolucion.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        // When: se solicita la devolucion
        Devolucion resultado = devolucionService.solicitar(request);

        // Then: la devolucion queda con los datos del pedido y motivo correctos
        assertNotNull(resultado);
        assertEquals(pedidoIdFake, resultado.getPedidoId());
        assertEquals(motivoFake, resultado.getMotivo());
        assertEquals(25000.0, resultado.getMontoDevuelto());
        verify(devolucionRepository, times(1)).save(any(Devolucion.class));
    }

    @Test
    void solicitar_debeLanzarExcepcion_cuandoPedidoNoExiste() {
        // Given: el pedido no existe en el microservicio Pedido
        when(pedidoClient.obtenerPedidoId(pedidoIdFake)).thenReturn(Map.of());

        // When / Then: se debe rechazar la solicitud de devolucion
        assertThrows(PedidoNoEncontradoException.class, () -> devolucionService.solicitar(request));
        verify(devolucionRepository, never()).save(any(Devolucion.class));
    }

    @Test
    void buscarPorId_debeLanzarExcepcion_cuandoNoExiste() {
        Integer idInexistente = faker.number().numberBetween(9000, 9999);
        when(devolucionRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(DevolucionNoEncontradaException.class, () -> devolucionService.buscarPorId(idInexistente));
    }

    @Test
    void aceptar_debeCambiarEstadoYRegistrarFecha() {
        Devolucion devolucionPendiente = new Devolucion();
        devolucionPendiente.setId(1);
        devolucionPendiente.setPedidoId(pedidoIdFake);
        devolucionPendiente.setMotivo(motivoFake);
        devolucionPendiente.setEstado("PENDIENTE");

        when(devolucionRepository.findById(1)).thenReturn(Optional.of(devolucionPendiente));
        when(devolucionRepository.save(any(Devolucion.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Devolucion resultado = devolucionService.aceptar(1);

        assertEquals("ACEPTADA", resultado.getEstado());
        assertEquals(LocalDate.now(), resultado.getFechaResolucion());
    }
}
