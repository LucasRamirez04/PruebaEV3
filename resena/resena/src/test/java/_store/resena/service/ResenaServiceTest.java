package _store.resena.service;

import _store.resena.dto.PromedioResponse;
import _store.resena.dto.ResenaRequest;
import _store.resena.exception.ClienteNoEncontradoException;
import _store.resena.exception.ProductoNoEncontradoException;
import _store.resena.exception.ResenaDuplicadaException;
import _store.resena.exception.ResenaNoEncontradaException;
import _store.resena.model.Resena;
import _store.resena.repository.ResenaRepository;
import _store.resena.webclient.ClienteClient;
import _store.resena.webclient.ProductoClient;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de ResenaService. ResenaRepository, ClienteClient y
 * ProductoClient se simulan con Mockito; Datafaker solo genera los datos
 * variables de cada request (comentarios, ids) sin necesidad de escribirlos
 * a mano.
 */
@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private ResenaService resenaService;

    private static final Faker faker = new Faker();

    private ResenaRequest crearRequestValido(Integer clienteId, Integer productoId) {
        ResenaRequest request = new ResenaRequest();
        request.setClienteId(clienteId);
        request.setProductoId(productoId);
        request.setCalificacion(faker.number().numberBetween(1, 6)); // 1 a 5
        request.setComentario(faker.lorem().sentence());
        return request;
    }

    // ------------------------------------------------------------------
    // guardar
    // ------------------------------------------------------------------

    @Test
    void guardar_conClienteYProductoValidos_debeValidarYGuardarLaResena() {
        // Given
        ResenaRequest request = crearRequestValido(1, 10);

        when(clienteClient.obtenerClienteId(1)).thenReturn(Map.of("id", 1, "nombre", "Lucas"));
        when(productoClient.obtenerProductoId(10)).thenReturn(Map.of("id", 10, "nombre", "Teclado"));
        when(resenaRepository.existsByClienteIdAndProductoId(1, 10)).thenReturn(false);
        when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> {
            Resena r = invocation.getArgument(0);
            r.setId(1);
            return r;
        });

        // When
        Resena resultado = resenaService.guardar(request);

        // Then
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getCalificacion()).isEqualTo(request.getCalificacion());
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }

    @Test
    void guardar_conClienteInexistente_debeLanzarClienteNoEncontradoExceptionYNoGuardar() {
        // Given: el microservicio Cliente responde error
        ResenaRequest request = crearRequestValido(999, 10);
        when(clienteClient.obtenerClienteId(999)).thenThrow(new RuntimeException("Cliente no encontrado"));

        // When / Then
        assertThrows(ClienteNoEncontradoException.class, () -> resenaService.guardar(request));
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void guardar_conProductoInexistente_debeLanzarProductoNoEncontradoExceptionYNoGuardar() {
        // Given: el cliente existe, pero el producto no
        ResenaRequest request = crearRequestValido(1, 999);
        when(clienteClient.obtenerClienteId(1)).thenReturn(Map.of("id", 1));
        when(productoClient.obtenerProductoId(999)).thenThrow(new RuntimeException("Producto no encontrado"));

        // When / Then
        assertThrows(ProductoNoEncontradoException.class, () -> resenaService.guardar(request));
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void guardar_conResenaDuplicada_debeLanzarResenaDuplicadaExceptionYNoGuardar() {
        // Given: el cliente ya reseñó este producto antes
        ResenaRequest request = crearRequestValido(1, 10);
        when(clienteClient.obtenerClienteId(1)).thenReturn(Map.of("id", 1));
        when(productoClient.obtenerProductoId(10)).thenReturn(Map.of("id", 10));
        when(resenaRepository.existsByClienteIdAndProductoId(1, 10)).thenReturn(true);

        // When / Then
        assertThrows(ResenaDuplicadaException.class, () -> resenaService.guardar(request));
        verify(resenaRepository, never()).save(any());
    }

    // ------------------------------------------------------------------
    // buscarPorId / listarPorProducto
    // ------------------------------------------------------------------

    @Test
    void buscarPorId_conIdInexistente_debeLanzarResenaNoEncontradaException() {
        when(resenaRepository.findById(404)).thenReturn(Optional.empty());
        assertThrows(ResenaNoEncontradaException.class, () -> resenaService.buscarPorId(404));
    }

    @Test
    void listarPorProducto_debeRetornarSoloLasResenasDeEseProducto() {
        // Given
        Resena r1 = new Resena(1, 1, 10, 5, "Excelente", null);
        Resena r2 = new Resena(2, 2, 10, 4, "Muy bueno", null);
        when(resenaRepository.findByProductoId(10)).thenReturn(List.of(r1, r2));

        // When
        List<Resena> resultado = resenaService.listarPorProducto(10);

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(r -> r.getProductoId().equals(10));
    }

    // ------------------------------------------------------------------
    // calcularPromedio
    // ------------------------------------------------------------------

    @Test
    void calcularPromedio_conResenasExistentes_debeRetornarElPromedioRedondeado() {
        // Given: dos reseñas con calificación 5 y 4 -> promedio 4.5
        Resena r1 = new Resena(1, 1, 10, 5, "a", null);
        Resena r2 = new Resena(2, 2, 10, 4, "b", null);
        when(resenaRepository.findByProductoId(10)).thenReturn(List.of(r1, r2));
        when(resenaRepository.calcularPromedioPorProducto(10)).thenReturn(4.5);

        // When
        PromedioResponse resultado = resenaService.calcularPromedio(10);

        // Then
        assertThat(resultado.getPromedio()).isEqualTo(4.5);
        assertThat(resultado.getTotalResenas()).isEqualTo(2L);
    }

    @Test
    void calcularPromedio_sinResenas_debeRetornarCeroEnVezDeNull() {
        // Given: producto sin reseñas todavía
        when(resenaRepository.findByProductoId(99)).thenReturn(List.of());
        when(resenaRepository.calcularPromedioPorProducto(99)).thenReturn(null);

        // When
        PromedioResponse resultado = resenaService.calcularPromedio(99);

        // Then: nunca debe romper con NullPointerException
        assertThat(resultado.getPromedio()).isEqualTo(0.0);
        assertThat(resultado.getTotalResenas()).isEqualTo(0L);
    }

    // ------------------------------------------------------------------
    // actualizar / eliminar
    // ------------------------------------------------------------------

    @Test
    void actualizar_conIdValido_debeCambiarCalificacionYComentario() {
        // Given
        Resena resenaExistente = new Resena(1, 1, 10, 3, "Comentario viejo", null);
        when(resenaRepository.findById(1)).thenReturn(Optional.of(resenaExistente));
        when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResenaRequest request = crearRequestValido(1, 10);
        request.setCalificacion(5);
        request.setComentario("Comentario actualizado");

        // When
        Resena resultado = resenaService.actualizar(1, request);

        // Then
        assertThat(resultado.getCalificacion()).isEqualTo(5);
        assertThat(resultado.getComentario()).isEqualTo("Comentario actualizado");
    }

    @Test
    void eliminar_conIdValido_debeEliminarLaResena() {
        Resena resena = new Resena(1, 1, 10, 5, "a", null);
        when(resenaRepository.findById(1)).thenReturn(Optional.of(resena));

        resenaService.eliminar(1);

        verify(resenaRepository, times(1)).delete(resena);
    }

    @Test
    void eliminar_conIdInexistente_debeLanzarResenaNoEncontradaExceptionYNoEliminarNada() {
        when(resenaRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(ResenaNoEncontradaException.class, () -> resenaService.eliminar(404));
        verify(resenaRepository, never()).delete(any());
    }
}
