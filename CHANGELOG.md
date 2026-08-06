# Changelog

All notable changes to the `ticket-service` microservice will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-08-06

### Added
- **Consumo Asíncrono de Eventos de Cierre (`StayClosedEvent`):** Implementado `TicketEventListenerAdapter` con `@RabbitListener` escuchando en `ticket-service-stay-closed-queue` para la emisión automática del ticket de salida tras el check-out en `stay-service`.
- **Garantía de Idempotencia & Unicidad (IN-20):** Protección contra duplicados mediante comprobación previa (`TicketAlreadyExistsException`) y restricción de unicidad en base de datos (`uk_stay_id`) asegurando la relación 1:1 entre estancia y ticket.
- **Resiliencia & Dead Letter Queue (DLQ):** Configuración de reintentos exponenciales (6 intentos) y enrutamiento automático a la cola de mensajes muertos `ticket-service-stay-closed-dlq`.
- **Gestión de Tickets de Entrada (`EntryTicket`):** Dominio y controladores REST (`/v1/entry-tickets`) para la creación, consulta y actualización de tickets de entrada emitidos en la barrera.
- **Integración con Keycloak & Spring Security:** OAuth2 Resource Server para la validación de Bearer Access Tokens emitidos por Keycloak.
- **Enrutamiento por API Gateway (Kong):** Enrutamiento centralizado y comprobación de seguridad en el perímetro a través de Kong.
- **Timeout de Bloqueo Pesimista:** Configuración de `lock_timeout = 2000` ms en HikariCP para evitar bloqueos indefinidos en PostgreSQL.
- **Trazabilidad Distribuida & Logging (GW-06):** Inclusión de `CorrelationIdFilter`, `RequestIdentityFilter` y `RequestLoggingFilter` inyectando `correlationId`, `userName` y `clientId` en el MDC.

### Changed
- **Formato Común de Errores (SEC-11 / RFC 7807):** Estandarización de respuestas de error devolviendo `ProblemDetail` / `ErrorResponse` uniforme.
- **Control de Acceso basado en Roles (RBAC):** Restricción de endpoints según matriz `SEC-03` (`ADMIN`/`OPERARIO` para emisión y consultas; `USER` para lectura por código).
- **Base de Datos Dedicada:** Perfil `prod` con PostgreSQL dedicada en puerto 5436.

### Fixed
- **Prevención de Duplicados por Concurrencia:** Tratamiento explícito de `DataIntegrityViolationException` en la restricción `uk_stay_id` permitiendo ignorar eventos concurrentes en el listener.
- **Manejo de Respuestas de Autenticación (401 / 403):** Emisión de cabecera `WWW-Authenticate` en respuestas 401.

### Security
- **Protección con `@PreAuthorize`:** Control de acceso en controladores REST.
- **Escaneo Continuo de Vulnerabilidades:** Pipeline CI/CD integrado con Trivy (`docker-scan`).

## [0.5.0] - 2026-07-29

### Added
- **MVP Inicial de `ticket-service`:** Implementación inicial de la arquitectura hexagonal para la emisión de tickets de cobro.
- **Endpoints REST Síncronos:**
  - `POST /v1/tickets`: Generación de ticket de salida.
  - `GET /v1/tickets`: Listado de tickets.
  - `GET /v1/tickets/{id}`: Consulta por UUID.
- **Persistencia PostgreSQL:** Configuración JPA con esquema `ticket`.
- **Contrato OpenAPI:** Especificación en `openapi.yml`.

[1.0.0]: https://github.com/tartis-academy/tartis-recon-ai-parking-ticketService/compare/v0.5.0...v1.0.0
[0.5.0]: https://github.com/tartis-academy/tartis-recon-ai-parking-ticketService/releases/tag/v0.5.0
