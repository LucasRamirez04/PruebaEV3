package _store.resena.controller;

import _store.resena.assembler.ResenaModelAssembler;
import _store.resena.dto.PromedioResponse;
import _store.resena.dto.ResenaRequest;
import _store.resena.model.Resena;
import _store.resena.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/store/resenas")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Opiniones y calificaciones de los clientes sobre los productos")
public class ResenaController {

    private final ResenaService resenaService;
    private final ResenaModelAssembler resenaModelAssembler;

    @Operation(summary = "Listar todas las reseñas registradas en el sistema")
    @ApiResponse(responseCode = "200", description = "Listado de reseñas")
    @GetMapping("/listar")
    public ResponseEntity<CollectionModel<EntityModel<Resena>>> listarTodas() {
        List<EntityModel<Resena>> resenas = resenaService.listarTodas().stream()
                .map(resenaModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(resenas));
    }

    @Operation(summary = "Buscar una reseña por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseña encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una reseña con ese ID", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Resena>> buscarPorId(
            @Parameter(description = "ID de la reseña") @PathVariable Integer id) {
        Resena resena = resenaService.buscarPorId(id);
        return ResponseEntity.ok(resenaModelAssembler.toModel(resena));
    }

    @Operation(summary = "Listar las reseñas de un producto específico")
    @ApiResponse(responseCode = "200", description = "Listado de reseñas del producto")
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<CollectionModel<EntityModel<Resena>>> listarPorProducto(
            @Parameter(description = "ID del producto") @PathVariable Integer productoId) {
        List<EntityModel<Resena>> resenas = resenaService.listarPorProducto(productoId).stream()
                .map(resenaModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(resenas));
    }

    @Operation(summary = "Obtener el promedio de calificación de un producto",
            description = "Regla de negocio calculada en base a todas las reseñas registradas para ese producto.")
    @ApiResponse(responseCode = "200", description = "Promedio calculado (0.0 si el producto no tiene reseñas)")
    @GetMapping("/producto/{productoId}/promedio")
    public ResponseEntity<PromedioResponse> obtenerPromedio(
            @Parameter(description = "ID del producto") @PathVariable Integer productoId) {
        return ResponseEntity.ok(resenaService.calcularPromedio(productoId));
    }

    @Operation(summary = "Registrar una nueva reseña",
            description = "Valida que el cliente y el producto existan, y que el cliente no haya reseñado "
                    + "antes ese mismo producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reseña creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "El cliente o el producto no existen", content = @Content),
            @ApiResponse(responseCode = "409", description = "El cliente ya reseñó este producto", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Resena>> guardar(@Valid @RequestBody ResenaRequest request) {
        Resena resenaGuardada = resenaService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaModelAssembler.toModel(resenaGuardada));
    }

    @Operation(summary = "Actualizar la calificación o el comentario de una reseña")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseña actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe una reseña con ese ID", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Resena>> actualizar(
            @Parameter(description = "ID de la reseña") @PathVariable Integer id,
            @Valid @RequestBody ResenaRequest request) {
        Resena resenaActualizada = resenaService.actualizar(id, request);
        return ResponseEntity.ok(resenaModelAssembler.toModel(resenaActualizada));
    }

    @Operation(summary = "Eliminar una reseña por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reseña eliminada"),
            @ApiResponse(responseCode = "404", description = "No existe una reseña con ese ID", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@Parameter(description = "ID de la reseña") @PathVariable Integer id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
