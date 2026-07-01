package _store.notificacion.service;

import _store.notificacion.exception.NotificacionNoEncontradaException;
import _store.notificacion.model.Notificacion;
import _store.notificacion.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    @Autowired
    NotificacionRepository notificacionRepository;

    public List<Notificacion> listarTodos() {
        log.info("Listando todas las notificaciones");
        return notificacionRepository.findAll();
    }

    public Notificacion buscarPorId(Integer id) {
        log.info("Buscando notificación con ID: {}", id);
        return notificacionRepository.findById(id)
                .orElseThrow(() -> new NotificacionNoEncontradaException("No se encontró la notificación con id: " + id));
    }

    public List<Notificacion> listarPorCliente(Integer clienteId) {
        log.info("Listando notificaciones del cliente ID: {}", clienteId);
        return notificacionRepository.findByClienteId(clienteId);
    }

    // método que llamarán otros microservicios (pedido, pago, envio) para crear una notificación
    public Notificacion crearDesdeEvento(Map<String, Object> datos) {
        Notificacion notificacion = new Notificacion();
        notificacion.setClienteId(((Number) datos.get("clienteId")).intValue());
        notificacion.setTipo((String) datos.get("tipo"));
        notificacion.setMensaje((String) datos.get("mensaje"));

        log.info("Creando notificación tipo {} para cliente ID: {}", notificacion.getTipo(), notificacion.getClienteId());

        // Aquí, en el futuro, podrías integrar un servicio real de email/SMS.
        // Por ahora, simulamos el envío con un log:
        log.info("📧 Notificación enviada -> Cliente {}: {}", notificacion.getClienteId(), notificacion.getMensaje());
        notificacion.setEstadoNotificacion("ENVIADA");

        return notificacionRepository.save(notificacion);
    }

    public void eliminar(Integer id) {
        log.info("Eliminando notificación con id: {}", id);
        Notificacion notificacion = buscarPorId(id);
        notificacionRepository.delete(notificacion);
    }
}