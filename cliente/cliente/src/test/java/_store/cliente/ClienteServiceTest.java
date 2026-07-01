package _store.cliente;

import _store.cliente.dto.ClienteRequest;
import _store.cliente.exception.ClienteNoEncontradoException;
import _store.cliente.model.Cliente;
import _store.cliente.repository.ClienteRepository;
import _store.cliente.service.ClienteService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

// Pruebas unitarias de ClienteService usando Datafaker para los datos
// y Mockito para simular ClienteRepository (sin tocar la base real).
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private final Faker faker = new Faker();
    private ClienteRequest request;

    @BeforeEach
    void setUp() {
        request = new ClienteRequest();
        request.setRun(faker.idNumber().valid());
        request.setNombre(faker.name().firstName());
        request.setApellido(faker.name().lastName());
        request.setCorreo(faker.internet().emailAddress());
        request.setTelefono("9" + faker.number().digits(8));
        request.setDireccion(faker.address().fullAddress());
    }

    @Test
    void crearDesdeRequest_debeGuardarCliente_conDatosDelRequest() {
        // Given
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        // When
        Cliente resultado = clienteService.crearDesdeRequest(request);

        // Then
        assertEquals(request.getRun(), resultado.getRun());
        assertEquals(request.getCorreo(), resultado.getCorreo());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void buscarPorId_debeLanzarExcepcion_cuandoClienteNoExiste() {
        int idFalso = faker.number().numberBetween(5000, 9000);
        when(clienteRepository.findById(idFalso)).thenReturn(Optional.empty());

        assertThrows(ClienteNoEncontradoException.class, () -> clienteService.buscarPorId(idFalso));
    }

    @Test
    void eliminar_debeBorrarCliente_cuandoExiste() {
        Cliente clienteExistente = new Cliente();
        clienteExistente.setId(1);
        clienteExistente.setRun(request.getRun());

        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteExistente));

        clienteService.eliminar(1);

        verify(clienteRepository, times(1)).delete(clienteExistente);
    }
}
