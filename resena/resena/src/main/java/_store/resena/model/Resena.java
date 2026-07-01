package _store.resena.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "resenas", uniqueConstraints = {
        // Regla de negocio: un cliente solo puede reseñar un mismo producto una vez.
        @UniqueConstraint(name = "uk_resena_cliente_producto", columnNames = {"cliente_id", "producto_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer clienteId;

    @Column(nullable = false)
    private Integer productoId;

    @Column(nullable = false)
    private Integer calificacion;

    @Column(length = 500)
    private String comentario;

    @Column(nullable = false)
    private LocalDate fechaResena;

    @PrePersist
    protected void onCreate() {
        this.fechaResena = LocalDate.now();
    }
}
