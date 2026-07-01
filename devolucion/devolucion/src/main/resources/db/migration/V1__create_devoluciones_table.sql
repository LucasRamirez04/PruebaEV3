CREATE TABLE devoluciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    motivo VARCHAR(200) NOT NULL,
    monto_devuelto DOUBLE NOT NULL,
    fecha_solicitud DATE NOT NULL,
    fecha_resolucion DATE NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
);
