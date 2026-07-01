package _store.cliente.controller;

import _store.cliente.dto.ClienteRequest;
import _store.cliente.model.Cliente;
import _store.cliente.service.ClienteService;
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

// Reemplaza al ClienteController original. Mismo flujo y mismas reglas,
// ahora con respuestas HATEOAS (EntityModel/CollectionModel), documentacion
// Swagger y la misma convencion que el microservicio Pedido (referencia del equipo).
@RestController
@RequestMapping("/api/store/clientes")
@Tag(name = "Clientes", description = "Gestion de clientes de 404Store")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/listar")
    @Operation(summary = "Listar todos los clientes")
    public ResponseEntity<CollectionModel<EntityModel<Cliente>>> listarClientes(){
        List<Cliente> clientes = clienteService.listarTodos();
        if (clientes.isEmpty()){
            CollectionModel<EntityModel<Cliente>> vacio = CollectionModel.empty();
            vacio.add(linkTo(methodOn(ClienteController.class).listarClientes()).withSelfRel());
            return ResponseEntity.ok(vacio);
        }

        List<EntityModel<Cliente>> modelos = clientes.stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Cliente>> coleccion = CollectionModel.of(modelos,
                linkTo(methodOn(ClienteController.class).listarClientes()).withSelfRel());

        return ResponseEntity.ok(coleccion);
    }

    @Operation(summary = "Buscar un cliente por su id")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> buscarPorID(@PathVariable Integer id){
        Cliente clienteEncontrado = clienteService.buscarPorId(id);
        return ResponseEntity.ok(toEntityModel(clienteEncontrado));
    }

    @Operation(summary = "Registrar un nuevo cliente")
    @PostMapping
    public ResponseEntity<EntityModel<Cliente>> guardar(@Valid @RequestBody ClienteRequest request){
        Cliente clienteGuardado = clienteService.crearDesdeRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toEntityModel(clienteGuardado));
    }

    @Operation(summary = "Actualizar los datos de un cliente")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> actualizar(@PathVariable Integer id, @Valid @RequestBody ClienteRequest request){
        Cliente clienteActualizado = clienteService.actualizar(id,request);
        return ResponseEntity.ok(toEntityModel(clienteActualizado));
    }

    @Operation(summary = "Eliminar un cliente por su id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id){
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Metodo auxiliar para construir el EntityModel con sus enlaces HATEOAS
    // (mismo patron que PedidoController, sin clase Assembler aparte).
    private EntityModel<Cliente> toEntityModel(Cliente cliente) {
        return EntityModel.of(cliente,
                linkTo(methodOn(ClienteController.class).buscarPorID(cliente.getId())).withSelfRel(),
                linkTo(methodOn(ClienteController.class).listarClientes()).withRel("clientes"));
    }
}
