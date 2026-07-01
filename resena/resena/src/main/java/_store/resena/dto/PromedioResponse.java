package _store.resena.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Promedio de calificación de un producto")
public class PromedioResponse {

    @Schema(example = "10")
    private Integer productoId;

    @Schema(example = "4.5")
    private Double promedio;

    @Schema(example = "8")
    private Long totalResenas;

    public PromedioResponse(Integer productoId, Double promedio, Long totalResenas) {
        this.productoId = productoId;
        this.promedio = promedio;
        this.totalResenas = totalResenas;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public Double getPromedio() {
        return promedio;
    }

    public Long getTotalResenas() {
        return totalResenas;
    }
}
