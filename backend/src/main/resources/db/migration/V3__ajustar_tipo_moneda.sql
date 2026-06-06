-- =============================================================================
-- V3: alinear el tipo de la columna 'moneda' con las entidades JPA.
--
-- En V1 cree 'moneda' como CHAR(3). Hibernate, al mapear el campo como
-- @Column(length = 3) sobre un String, espera VARCHAR(3). Con ddl-auto=validate
-- esa diferencia (CHAR vs VARCHAR) detiene el arranque.
--
-- Por que VARCHAR(3) y no CHAR(3): CHAR rellena con espacios a la derecha
-- ('US ' en vez de 'USD' si faltara un caracter) y obliga a recortar; VARCHAR no
-- rellena y es lo que Hibernate genera por defecto para String. Asi quedan
-- alineados esquema y entidades sin sorpresas.
--
-- USING moneda::varchar(3): convierte los valores existentes sin perder datos.
-- =============================================================================

ALTER TABLE producto_precio
    ALTER COLUMN moneda TYPE VARCHAR(3) USING moneda::varchar(3);

ALTER TABLE orden_item
    ALTER COLUMN moneda TYPE VARCHAR(3) USING moneda::varchar(3);
