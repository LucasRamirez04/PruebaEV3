CREATE TABLE notificaciones (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                cliente_id INT NOT NULL,
                                tipo VARCHAR(50) NOT NULL,
                                mensaje VARCHAR(255) NOT NULL,
                                estado_notificacion VARCHAR(50) NOT NULL,
                                fecha_envio DATE NOT NULL
);