-- =============================================================================
-- Preparacion de PostgreSQL para el reto (ejecutar UNA vez, como superusuario).
--
-- Como ejecutarlo (PowerShell), te pedira la clave del usuario 'postgres':
--   $env:Path += ";C:\Program Files\PostgreSQL\17\bin"
--   psql -U postgres -h localhost -f backend/db/setup.sql
--
-- Que hace cada sentencia:
--   * CREATE ROLE: crea el usuario de la app (NO superusuario, principio de
--     menor privilegio). LOGIN le permite conectarse; PASSWORD fija su clave.
--   * CREATE DATABASE: una base para desarrollo (reto_dev) y otra para los
--     tests de integracion (reto_test), aisladas entre si.
--   * GRANT ... ON DATABASE: permite a reto_user conectarse a cada base.
--   * \connect + GRANT ON SCHEMA public: en PostgreSQL 15+ el esquema public ya
--     no da permisos por defecto, asi que se los concedo explicitamente para que
--     Flyway pueda crear tablas.
-- =============================================================================

-- Usuario de la aplicacion (no recrear si ya existe daria error; ejecutar limpio).
CREATE ROLE reto_user WITH LOGIN PASSWORD 'reto_pass';

-- Bases de datos. OWNER reto_user para que sea dueno de sus objetos.
CREATE DATABASE reto_dev  OWNER reto_user;
CREATE DATABASE reto_test OWNER reto_user;

GRANT ALL PRIVILEGES ON DATABASE reto_dev  TO reto_user;
GRANT ALL PRIVILEGES ON DATABASE reto_test TO reto_user;

-- Permisos sobre el esquema public de cada base.
\connect reto_dev
GRANT ALL ON SCHEMA public TO reto_user;

\connect reto_test
GRANT ALL ON SCHEMA public TO reto_user;
