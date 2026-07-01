package _store.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar o actualizar una reseña")
public class ResenaRequest {

    @Schema(description = "ID del cliente que escribe la reseña", example = "3")
    @NotNull(message = "El ID del cliente es obligatorio")
    private Integer clienteId;

    @Schema(description = "ID del producto reseñado", example = "10")
    @NotNull(message = "El ID del producto es obligatorio")
    private Integer productoId;

    @Schema(description = "Calificación de 1 a 5 estrellas", example = "5")
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    @Schema(description = "Comentario opcional sobre el producto", example = "Excelente calidad, llegó antes de lo esperado")
    @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
    private String comentario;

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
