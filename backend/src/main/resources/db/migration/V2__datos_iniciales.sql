-- =============================================================================
-- V2 — DATOS INICIALES (seed)
-- =============================================================================
-- Carga catálogos base, los roles, los usuarios demo y datos de ejemplo para que
-- la aplicación sea demostrable apenas arranca.
--
-- IMPORTANTE sobre las contraseñas:
--   Los valores password_hash son hashes BCrypt (empiezan por $2a$). NUNCA se
--   guarda la contraseña en texto plano. Los hashes de abajo corresponden a:
--     admin@reto.com    -> Admin123*
--     externo@reto.com  -> Externo123*
--   (Estos hashes se generaron con BCryptPasswordEncoder, fuerza 10.)
-- =============================================================================

-- Roles del sistema.
INSERT INTO rol (nombre) VALUES ('ADMIN'), ('EXTERNAL');

-- Usuarios demo. Los hashes son BCrypt (fuerza 10) reales: empiezan por $2a$ e
-- incluyen su propia "sal" aleatoria, por eso no se pueden revertir a texto plano.
INSERT INTO usuario (correo, password_hash) VALUES
    ('admin@reto.com',   '$2a$10$nRRoHAtBvnwzIIGs9nt3..uIQEPelf4ICJFoBjFrrBpTCDvIPCApK'),
    ('externo@reto.com', '$2a$10$Lup6jSVhWqxEpAfqIcVUOOQruU.M38DfTPkeSaaSY6WYbuHe8dqrK');

-- Asignación de roles (subconsultas para no depender de los IDs autogenerados).
INSERT INTO usuario_rol (usuario_id, rol_id)
SELECT u.id, r.id FROM usuario u, rol r
WHERE u.correo = 'admin@reto.com' AND r.nombre = 'ADMIN';

INSERT INTO usuario_rol (usuario_id, rol_id)
SELECT u.id, r.id FROM usuario u, rol r
WHERE u.correo = 'externo@reto.com' AND r.nombre = 'EXTERNAL';

-- Empresas de ejemplo.
INSERT INTO empresa (nit, nombre, direccion, telefono) VALUES
    ('900123456-7', 'Tecnologia Andina SAS', 'Calle 100 #15-20, Bogota', '6011234567'),
    ('901987654-3', 'Distribuciones Caribe LTDA', 'Carrera 50 #30-10, Barranquilla', '6053216548');

-- Categorías de ejemplo.
INSERT INTO categoria (nombre, descripcion) VALUES
    ('Electronica', 'Dispositivos y componentes electronicos'),
    ('Hogar', 'Articulos para el hogar'),
    ('Oficina', 'Suministros de oficina');

-- Productos de ejemplo (asociados a empresas por su NIT).
INSERT INTO producto (codigo, nombre, caracteristicas, empresa_nit) VALUES
    ('PROD-001', 'Laptop Pro 14', 'CPU 8 nucleos, 16GB RAM, 512GB SSD', '900123456-7'),
    ('PROD-002', 'Mouse Inalambrico', 'Ergonomico, bluetooth, 1600 DPI', '900123456-7'),
    ('PROD-003', 'Silla Ergonomica', 'Soporte lumbar, reclinable', '901987654-3');

-- Precios multi-moneda (una fila por producto y moneda).
INSERT INTO producto_precio (producto_id, moneda, valor)
SELECT id, 'USD', 1200.00 FROM producto WHERE codigo = 'PROD-001';
INSERT INTO producto_precio (producto_id, moneda, valor)
SELECT id, 'COP', 4800000.00 FROM producto WHERE codigo = 'PROD-001';
INSERT INTO producto_precio (producto_id, moneda, valor)
SELECT id, 'USD', 25.00 FROM producto WHERE codigo = 'PROD-002';
INSERT INTO producto_precio (producto_id, moneda, valor)
SELECT id, 'COP', 100000.00 FROM producto WHERE codigo = 'PROD-002';
INSERT INTO producto_precio (producto_id, moneda, valor)
SELECT id, 'COP', 350000.00 FROM producto WHERE codigo = 'PROD-003';

-- Asociación producto-categoría (N:N).
INSERT INTO producto_categoria (producto_id, categoria_id)
SELECT p.id, c.id FROM producto p, categoria c
WHERE p.codigo = 'PROD-001' AND c.nombre IN ('Electronica', 'Oficina');
INSERT INTO producto_categoria (producto_id, categoria_id)
SELECT p.id, c.id FROM producto p, categoria c
WHERE p.codigo = 'PROD-002' AND c.nombre = 'Electronica';
INSERT INTO producto_categoria (producto_id, categoria_id)
SELECT p.id, c.id FROM producto p, categoria c
WHERE p.codigo = 'PROD-003' AND c.nombre = 'Hogar';

-- Inventario inicial.
INSERT INTO inventario (producto_id, cantidad)
SELECT id, 15 FROM producto WHERE codigo = 'PROD-001';
INSERT INTO inventario (producto_id, cantidad)
SELECT id, 120 FROM producto WHERE codigo = 'PROD-002';
INSERT INTO inventario (producto_id, cantidad)
SELECT id, 30 FROM producto WHERE codigo = 'PROD-003';

-- Cliente de ejemplo.
INSERT INTO cliente (nombre, correo, telefono) VALUES
    ('Juan Perez', 'juan.perez@correo.com', '3001234567');
