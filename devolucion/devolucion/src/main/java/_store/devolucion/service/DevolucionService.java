package _store.devolucion.service;

import _store.devolucion.dto.DevolucionRequest;
import _store.devolucion.exception.DevolucionNoEncontradaException;
import _store.devolucion.exception.PedidoNoEncontradoException;
import _store.devolucion.model.Devolucion;
import _store.devolucion.repository.DevolucionRepository;
import _store.devolucion.webclient.PedidoClient;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DevolucionService {

    private static final Logger log = LoggerFactory.getLogger(DevolucionService.class);

    @Autowired
    private DevolucionRepository devolucionRepository;

    @Autowired
    private PedidoClient pedidoClient;

    public List<Devolucion> listarTodas() {
        log.info("Listando todas las devoluciones");
        return devolucionRepository.findAll();
    }

    public Devolucion buscarPorId(Integer id) {
        log.info("Buscando devolucion con id: {}", id);
        return devolucionRepository.findById(id)
                .orElseThrow(() -> new DevolucionNoEncontradaException("No se encontro la devolucion con id: " + id));
    }

    public Devolucion solicitar(DevolucionRequest request) {
        log.info("Validando pedido {} antes de solicitar devolucion", request.getPedidoId());

        Map<String, Object> pedido = pedidoClient.obtenerPedidoId(request.getPedidoId());
        if (pedido == null || pedido.isEmpty()) {
            throw new PedidoNoEncontradoException("Pedido no encontrado, no se puede solicitar la devolucion");
        }

        Double total = pedido.get("total") != null ? ((Number) pedido.get("total")).doubleValue() : 0.0;

        Devolucion devolucion = new Devolucion();
        devolucion.setPedidoId(request.getPedidoId());
        devolucion.setMotivo(request.getMotivo());
        devolucion.setMontoDevuelto(total);

        Devolucion guardada = devolucionRepository.save(devolucion);
        log.info("Devolucion {} solicitada para el pedido {}", guardada.getId(), guardada.getPedidoId());
        return guardada;
    }

    public Devolucion aceptar(Integer id) {
        Devolucion devolucion = buscarPorId(id);
        devolucion.setEstado("ACEPTADA");
        devolucion.setFechaResolucion(LocalDate.now());
        log.info("Devolucion {} aceptada", id);
        return devolucionRepository.save(devolucion);
    }

    public Devolucion rechazar(Integer id) {
        Devolucion devolucion = buscarPorId(id);
        devolucion.setEstado("RECHAZADA");
        devolucion.setFechaResolucion(LocalDate.now());
        log.info("Devolucion {} rechazada", id);
        return devolucionRepository.save(devolucion);
    }

    public void eliminar(Integer id) {
        log.info("Eliminando devolucion con id: {}", id);
        Devolucion devolucion = buscarPorId(id);
        devolucionRepository.delete(devolucion);
    }
}
