package _store.notificacion.repository;

import _store.notificacion.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    // permite consultar todas las notificaciones de un cliente específico
    List<Notificacion> findByClienteId(Integer clienteId);
}