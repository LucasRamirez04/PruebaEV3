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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ResenaService {

    private static final Logger log = LoggerFactory.getLogger(ResenaService.class);

    private final ResenaRepository resenaRepository;
    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;

    public List<Resena> listarTodas() {
        return resenaRepository.findAll();
    }

    public Resena buscarPorId(Integer id) {
        return resenaRepository.findById(id)
                .orElseThrow(() -> new ResenaNoEncontradaException("No se encontró la reseña con ID: " + id));
    }

    public List<Resena> listarPorProducto(Integer productoId) {
        return resenaRepository.findByProductoId(productoId);
    }

    /**
     * Crea una nueva reseña aplicando las reglas de negocio del dominio:
     *  1) El cliente debe existir (se valida contra el microservicio Cliente).
     *  2) El producto debe existir (se valida contra el microservicio Producto).
     *  3) Un mismo cliente no puede reseñar dos veces el mismo producto.
     */
    public Resena guardar(ResenaRequest request) {
        validarClienteExistente(request.getClienteId());
        validarProductoExistente(request.getProductoId());

        if (resenaRepository.existsByClienteIdAndProductoId(request.getClienteId(), request.getProductoId())) {
            throw new ResenaDuplicadaException(
                    "El cliente " + request.getClienteId() + " ya reseñó el producto " + request.getProductoId());
        }

        Resena resena = new Resena();
        resena.setClienteId(request.getClienteId());
        resena.setProductoId(request.getProductoId());
        resena.setCalificacion(request.getCalificacion());
        resena.setComentario(request.getComentario());

        return resenaRepository.save(resena);
    }

    /**
     * Permite corregir la calificación o el comentario de una reseña ya existente.
     * No se permite cambiar el clienteId ni el productoId: para eso se elimina y se crea una nueva.
     */
    public Resena actualizar(Integer id, ResenaRequest request) {
        Resena resena = buscarPorId(id);
        resena.setCalificacion(request.getCalificacion());
        resena.setComentario(request.getComentario());
        return resenaRepository.save(resena);
    }

    public void eliminar(Integer id) {
        Resena resena = buscarPorId(id);
        resenaRepository.delete(resena);
    }

    /**
     * Calcula el promedio de calificación de un producto. Si el producto todavía
     * no tiene reseñas, retorna promedio 0.0 en vez de null (evita NullPointerException
     * en el cliente que consuma este endpoint).
     */
    public PromedioResponse calcularPromedio(Integer productoId) {
        List<Resena> resenas = resenaRepository.findByProductoId(productoId);
        Double promedio = resenaRepository.calcularPromedioPorProducto(productoId);
        double promedioFinal = (promedio != null) ? Math.round(promedio * 10) / 10.0 : 0.0;

        return new PromedioResponse(productoId, promedioFinal, (long) resenas.size());
    }

    // ------------------------------------------------------------------
    // Métodos privados de apoyo
    // ------------------------------------------------------------------

    private void validarClienteExistente(Integer clienteId) {
        try {
            Map<String, Object> cliente = clienteClient.obtenerClienteId(clienteId);
            if (cliente == null || cliente.isEmpty()) {
                throw new ClienteNoEncontradoException("No se encontró el cliente con ID: " + clienteId);
            }
        } catch (ClienteNoEncontradoException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("No se pudo validar el cliente {}: {}", clienteId, ex.getMessage());
            throw new ClienteNoEncontradoException("No se pudo validar el cliente con ID: " + clienteId);
        }
    }

    private void validarProductoExistente(Integer productoId) {
        try {
            Map<String, Object> producto = productoClient.obtenerProductoId(productoId);
            if (producto == null || producto.isEmpty()) {
                throw new ProductoNoEncontradoException("No se encontró el producto con ID: " + productoId);
            }
        } catch (ProductoNoEncontradoException ex) {
            throw ex;
        } catch (Exception ex) {
            log.warn("No se pudo validar el producto {}: {}", productoId, ex.getMessage());
            throw new ProductoNoEncontradoException("No se pudo validar el producto con ID: " + productoId);
        }
    }
}
