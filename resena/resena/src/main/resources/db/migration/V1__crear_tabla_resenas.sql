CREATE TABLE resenas (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id      INT             NOT NULL,
    producto_id     INT             NOT NULL,
    calificacion    INT             NOT NULL,
    comentario      VARCHAR(500),
    fecha_resena    DATE            NOT NULL,
    CONSTRAINT uk_resena_cliente_producto UNIQUE (cliente_id, producto_id)
);
