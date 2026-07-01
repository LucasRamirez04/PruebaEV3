package _store.notificacion.controller;

import _store.notificacion.model.Notificacion;
import _store.notificacion.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Notificaciones", description = "Gestión de notificaciones de eventos del sistema (pedido, pago, envío)")
@RestController
@RequestMapping("/api/store/notificaciones")
public class NotificacionController {

    @Autowired
    NotificacionService notificacionService;

    @Operation(summary = "Listar todas las notificaciones registradas")
    @GetMapping("/listar")
    public ResponseEntity<List<Notificacion>> listarNotificaciones() {
        List<Notificacion> notificaciones = notificacionService.listarTodos();
        if (notificaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notificaciones);
    }

    @Operation(summary = "Buscar una notificación por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> buscarPorId(@PathVariable Integer id) {
        Notificacion notificacion = notificacionService.buscarPorId(id);
        return ResponseEntity.ok(notificacion);
    }

    @Operation(summary = "Listar todas las notificaciones de un cliente específico")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Notificacion>> listarPorCliente(@PathVariable Integer clienteId) {
        List<Notificacion> notificaciones = notificacionService.listarPorCliente(clienteId);
        if (notificaciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notificaciones);
    }

    @Operation(summary = "Crear una notificación a partir de un evento (pedido, pago o envío)")
    @PostMapping
    public ResponseEntity<Notificacion> crearNotificacion(@RequestBody Map<String, Object> datos) {
        Notificacion notificacionGuardada = notificacionService.crearDesdeEvento(datos);
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionGuardada);
    }

    @Operation(summary = "Eliminar una notificación por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}