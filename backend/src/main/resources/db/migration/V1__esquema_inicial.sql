-- =============================================================================
-- V1 — ESQUEMA INICIAL
-- =============================================================================
-- Flyway ejecuta este script una sola vez y registra su versión en la tabla
-- flyway_schema_history. El nombre sigue el patrón V<numero>__<descripcion>.sql
-- (doble guion bajo). El orden lo da el número de versión.
--
-- Convenciones:
--   * snake_case, tablas en singular.
--   * BIGINT GENERATED ALWAYS AS IDENTITY (estándar SQL) en vez de SERIAL.
--   * NUMERIC para dinero (exacto), TIMESTAMPTZ para fechas (con zona horaria).
--   * Índice explícito en cada FK (PostgreSQL no lo crea solo).
-- =============================================================================

-- ----------------------------------------------------------------------------
-- SEGURIDAD: rol, usuario, usuario_rol
-- ----------------------------------------------------------------------------
CREATE TABLE rol (
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE usuario (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    correo        VARCHAR(150) NOT NULL UNIQUE,
    -- Guardo el HASH BCrypt (60 chars), nunca texto plano. VARCHAR(255) deja
    -- margen por si migro a Argon2 en el futuro.
    password_hash VARCHAR(255) NOT NULL,
    activo        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE usuario_rol (
    usuario_id BIGINT NOT NULL,
    rol_id     BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_rol_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_rol_rol
        FOREIGN KEY (rol_id) REFERENCES rol (id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- NEGOCIO: empresa (PK natural = NIT)
-- ----------------------------------------------------------------------------
CREATE TABLE empresa (
    nit        VARCHAR(20) PRIMARY KEY,
    nombre     VARCHAR(150) NOT NULL,
    direccion  VARCHAR(200),
    telefono   VARCHAR(30),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ----------------------------------------------------------------------------
-- NEGOCIO: categoria
-- ----------------------------------------------------------------------------
CREATE TABLE categoria (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- ----------------------------------------------------------------------------
-- NEGOCIO: producto
-- ----------------------------------------------------------------------------
CREATE TABLE producto (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo          VARCHAR(50) NOT NULL UNIQUE,
    nombre          VARCHAR(150) NOT NULL,
    caracteristicas TEXT,
    empresa_nit     VARCHAR(20) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    -- RESTRICT: no se puede borrar una empresa que aún tiene productos.
    CONSTRAINT fk_producto_empresa
        FOREIGN KEY (empresa_nit) REFERENCES empresa (nit) ON DELETE RESTRICT
);
CREATE INDEX idx_producto_empresa_nit ON producto (empresa_nit);

-- ----------------------------------------------------------------------------
-- NEGOCIO: producto_precio (multi-moneda)
-- ----------------------------------------------------------------------------
CREATE TABLE producto_precio (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    moneda      CHAR(3) NOT NULL,                 -- código ISO 4217: USD, COP, EUR
    valor       NUMERIC(15,2) NOT NULL,           -- dinero exacto, nunca float
    CONSTRAINT chk_precio_no_negativo CHECK (valor >= 0),
    CONSTRAINT uq_producto_moneda UNIQUE (producto_id, moneda),
    CONSTRAINT fk_precio_producto
        FOREIGN KEY (producto_id) REFERENCES producto (id) ON DELETE CASCADE
);
CREATE INDEX idx_precio_producto_id ON producto_precio (producto_id);

-- ----------------------------------------------------------------------------
-- NEGOCIO: producto_categoria (N:N)
-- ----------------------------------------------------------------------------
CREATE TABLE producto_categoria (
    producto_id  BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    PRIMARY KEY (producto_id, categoria_id),
    CONSTRAINT fk_pc_producto
        FOREIGN KEY (producto_id) REFERENCES producto (id) ON DELETE CASCADE,
    CONSTRAINT fk_pc_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria (id) ON DELETE CASCADE
);
CREATE INDEX idx_pc_categoria_id ON producto_categoria (categoria_id);

-- ----------------------------------------------------------------------------
-- NEGOCIO: inventario (un registro por producto)
-- ----------------------------------------------------------------------------
CREATE TABLE inventario (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    producto_id BIGINT NOT NULL UNIQUE,
    cantidad    INTEGER NOT NULL DEFAULT 0,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_cantidad_no_negativa CHECK (cantidad >= 0),
    CONSTRAINT fk_inventario_producto
        FOREIGN KEY (producto_id) REFERENCES producto (id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- NEGOCIO: cliente
-- ----------------------------------------------------------------------------
CREATE TABLE cliente (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre     VARCHAR(150) NOT NULL,
    correo     VARCHAR(150) NOT NULL UNIQUE,
    telefono   VARCHAR(30),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ----------------------------------------------------------------------------
-- NEGOCIO: orden y orden_item
-- ----------------------------------------------------------------------------
CREATE TABLE orden (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    fecha      TIMESTAMPTZ NOT NULL DEFAULT now(),
    estado     VARCHAR(20) NOT NULL DEFAULT 'CREADA',
    CONSTRAINT chk_orden_estado
        CHECK (estado IN ('CREADA', 'PAGADA', 'ENVIADA', 'CANCELADA')),
    CONSTRAINT fk_orden_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente (id) ON DELETE RESTRICT
);
CREATE INDEX idx_orden_cliente_id ON orden (cliente_id);

CREATE TABLE orden_item (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    orden_id        BIGINT NOT NULL,
    producto_id     BIGINT NOT NULL,
    cantidad        INTEGER NOT NULL,
    -- Snapshot del precio al momento de la compra (puede cambiar después).
    precio_unitario NUMERIC(15,2) NOT NULL,
    moneda          CHAR(3) NOT NULL,
    CONSTRAINT chk_item_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_item_precio CHECK (precio_unitario >= 0),
    CONSTRAINT uq_orden_producto UNIQUE (orden_id, producto_id),
    CONSTRAINT fk_item_orden
        FOREIGN KEY (orden_id) REFERENCES orden (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_producto
        FOREIGN KEY (producto_id) REFERENCES producto (id) ON DELETE RESTRICT
);
CREATE INDEX idx_item_orden_id ON orden_item (orden_id);
CREATE INDEX idx_item_producto_id ON orden_item (producto_id);
