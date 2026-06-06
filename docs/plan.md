Voy a armar una app con backend en Spring Boot y frontend en Quasar
La idea es que tenga login con correo y contraseña dos roles ADMIN y EXTERNAL
CRUD de empresas con NIT como llave primaria CRUD de productos por empresa con categorías y precios en varias monedas
El modelo de datos incluye empresas productos categorías clientes órdenes e inventario
El inventario se puede consultar descargar en PDF y enviar por correo usando una API externa

Stack
Backend Java 21 Spring Boot 3.x Maven Spring Web Spring Security Spring Data JPA Hibernate PostgreSQL Flyway Bean Validation springdoc-openapi Actuator JUnit 5 Mockito
Lombok y MapStruct solo donde aporten
Frontend Vue 3 Quasar TypeScript Pinia Axios Vue Router ESLint Prettier
AWS (opcional) Elastic Beanstalk para el JAR RDS PostgreSQL S3 + CloudFront Parameter Store/Secrets Manager CloudWatch Terraform
Región us-east-1

Decisiones técnicas
Arquitectura monolito modular en capas controller service repository organizado por feature
Patrón puerto/adaptador solo para email y PDF
Seguridad JWT stateless contraseñas con BCrypt autorización por rol
Base de datos Flyway para migraciones en local ddl-auto=validate después de Flyway
Perfiles local test prod
Secretos fuera del repositorio
DTOs separados de entidades JPA para no exponer el modelo interno
Dinero NUMERIC/BigDecimal fechas TIMESTAMPTZ
Multi-moneda y N:N producto-categoría con tablas normalizadas

Qué NO
Docker WSL Testcontainers no son necesarios
Microservicios Kubernetes Kafka CQRS Event Sourcing sobreingeniería
ECS Fargate requiere Docker uso Elastic Beanstalk con JAR
ddl-auto=update en producción riesgo uso Flyway
Conversión de moneda en vivo y UI de clientes/órdenes quedan para después

Entorno local
PostgreSQL nativo en Windows bases de datos para desarrollo y pruebas
Backend con mvn spring-boot:run frontend con quasar dev
Sin contenedores
