package _store.resena.repository;

import _store.resena.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Integer> {

    List<Resena> findByProductoId(Integer productoId);

    boolean existsByClienteIdAndProductoId(Integer clienteId, Integer productoId);

    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.productoId = :productoId")
    Double calcularPromedioPorProducto(@Param("productoId") Integer productoId);
}
