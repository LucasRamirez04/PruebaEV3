package _store.resena.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import _store.resena.controller.ResenaController;
import _store.resena.model.Resena;

@Component
public class ResenaModelAssembler implements RepresentationModelAssembler<Resena, EntityModel<Resena>> {

    @Override
    public EntityModel<Resena> toModel(Resena resena) {
        return EntityModel.of(resena,
                linkTo(methodOn(ResenaController.class).buscarPorId(resena.getId())).withSelfRel(),
                linkTo(methodOn(ResenaController.class).listarTodas()).withRel("resenas"),
                linkTo(methodOn(ResenaController.class).listarPorProducto(resena.getProductoId()))
                        .withRel("resenasDelProducto"),
                linkTo(methodOn(ResenaController.class).obtenerPromedio(resena.getProductoId()))
                        .withRel("promedioProducto"),
                linkTo(methodOn(ResenaController.class).eliminar(resena.getId())).withRel("eliminar"));
    }
}
