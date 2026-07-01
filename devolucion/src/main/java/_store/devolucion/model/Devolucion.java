package _store.devolucion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "devoluciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer pedidoId;

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private Double montoDevuelto;

    @Column(nullable = false)
    private LocalDate fechaSolicitud;

    @Column(nullable = true)
    private LocalDate fechaResolucion;

    @Column(nullable = false)
    private String estado; // PENDIENTE, ACEPTADA, RECHAZADA

    @PrePersist
    protected void onCreate() {
        this.fechaSolicitud = LocalDate.now();
        if (this.estado == null) this.estado = "PENDIENTE";
    }
}
