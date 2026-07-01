package _store.devolucion.controller;

import _store.devolucion.dto.DevolucionRequest;
import _store.devolucion.model.Devolucion;
import _store.devolucion.service.DevolucionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// Misma convencion que el microservicio Pedido (referencia del equipo):
// /listar para listados, 200 OK con coleccion vacia, metodo privado para
// armar el EntityModel (sin clase Assembler aparte). Se preservan los
// enlaces condicionales de aceptar/rechazar que solo aplican si esta PENDIENTE.
@RestController
@RequestMapping("/api/store/devoluciones")
@Tag(name = "Devoluciones", description = "Gestion de solicitudes de devolucion de pedidos")
public class DevolucionController {

    @Autowired
    private DevolucionService devolucionService;

    @Operation(summary = "Listar todas las devoluciones registradas")
    @GetMapping("/listar")
    public ResponseEntity<CollectionModel<EntityModel<Devolucion>>> listar() {
        List<Devolucion> devoluciones = devolucionService.listarTodas();
        if (devoluciones.isEmpty()) {
            CollectionModel<EntityModel<Devolucion>> vacio = CollectionModel.empty();
            vacio.add(linkTo(methodOn(DevolucionController.class).listar()).withSelfRel());
            return ResponseEntity.ok(vacio);
        }

        List<EntityModel<Devolucion>> modelos = devoluciones.stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Devolucion>> coleccion = CollectionModel.of(modelos,
                linkTo(methodOn(DevolucionController.class).listar()).withSelfRel());

        return ResponseEntity.ok(coleccion);
    }

    @Operation(summary = "Buscar una devolucion por su id")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Devolucion>> buscarPorId(@PathVariable Integer id) {
        Devolucion devolucion = devolucionService.buscarPorId(id);
        return ResponseEntity.ok(toEntityModel(devolucion));
    }

    @Operation(summary = "Solicitar una devolucion para un pedido existente")
    @PostMapping
    public ResponseEntity<EntityModel<Devolucion>> solicitar(@Valid @RequestBody DevolucionRequest request) {
        Devolucion guardada = devolucionService.solicitar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toEntityModel(guardada));
    }

    @Operation(summary = "Aceptar una devolucion pendiente")
    @PatchMapping("/{id}/aceptar")
    public ResponseEntity<EntityModel<Devolucion>> aceptar(@PathVariable Integer id) {
        Devolucion actualizada = devolucionService.aceptar(id);
        return ResponseEntity.ok(toEntityModel(actualizada));
    }

    @Operation(summary = "Rechazar una devolucion pendiente")
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<EntityModel<Devolucion>> rechazar(@PathVariable Integer id) {
        Devolucion actualizada = devolucionService.rechazar(id);
        return ResponseEntity.ok(toEntityModel(actualizada));
    }

    @Operation(summary = "Eliminar una devolucion por su id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        devolucionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Metodo auxiliar para construir el EntityModel con sus enlaces HATEOAS.
    private EntityModel<Devolucion> toEntityModel(Devolucion devolucion) {
        EntityModel<Devolucion> modelo = EntityModel.of(devolucion,
                linkTo(methodOn(DevolucionController.class).buscarPorId(devolucion.getId())).withSelfRel(),
                linkTo(methodOn(DevolucionController.class).listar()).withRel("devoluciones"));

        // Solo se ofrecen acciones de aceptar/rechazar si sigue pendiente
        if ("PENDIENTE".equals(devolucion.getEstado())) {
            modelo.add(linkTo(methodOn(DevolucionController.class).aceptar(devolucion.getId())).withRel("aceptar"));
            modelo.add(linkTo(methodOn(DevolucionController.class).rechazar(devolucion.getId())).withRel("rechazar"));
        }

        return modelo;
    }
}
