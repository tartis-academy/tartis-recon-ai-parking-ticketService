# Alcance de la Fase II (v1.0.0) — ticket-service

Documento explicativo del alcance, responsabilidad, modelo de dominio, endpoints expuestos, mensajería asíncrona, seguridad e infraestructura del microservicio `ticket-service` durante la **Fase II (v1.0.0)** del sistema de parking inteligente **TARTIS Recon-AI**.

---

## 1. Responsabilidad del Microservicio en Fase II

En la Fase II, `ticket-service` amplía sus responsabilidades gestionando el ciclo completo de tickets de entrada y cobro:
- **Consumo Asíncrono de Eventos de Cierre (`StayClosedEvent`):** Implementación de `TicketEventListenerAdapter` con `@RabbitListener` escuchando en `ticket-service-stay-closed-queue` para emitir automáticamente el ticket de salida tras el check-out en `stay-service`.
- **Garantía de Idempotencia & Unicidad (IN-20):** Protección contra duplicados mediante comprobación previa (`TicketAlreadyExistsException`) y restricción de unicidad en base de datos (`uk_stay_id`) asegurando la relación 1:1 entre estancia y ticket de salida.
- **Resiliencia & Dead Letter Queue (DLQ):** Reintentos exponenciales (6 intentos) y enrutamiento a `ticket-service-stay-closed-dlq`.
- **Gestión de Tickets de Entrada (`EntryTicket`):** Dominio y controladores REST (`/v1/entry-tickets`) para la creación, consulta y actualización de tickets de entrada emitidos en la barrera.
- **Gestión de Tickets Perdidos (IN-22):** Endpoint `PATCH /v1/tickets/{ticketId}/lost` para marcar tickets como extraviados evitando recálculos con tarifa ordinaria.
- **Timeout de Bloqueo Pesimista:** Configuración de `lock_timeout = 2000` ms en HikariCP.
- **Seguridad OAuth2 / Keycloak & Kong:** Resource Server para validación de tokens JWT y roles RBAC (`ADMIN`, `OPERARIO`, `USER`).

---

## 2. Modelo de Dominio y Persistencia (Fase II)

Entidades **`EntryTicket`** y **`Ticket`**:

| Entidad | Atributo | Tipo | Descripción | Validación / Restricción |
|---|---|---|---|---|
| `EntryTicket` | `id`, `stayId`, `code`, `entryTime` | `UUID`, `String`, `LocalDateTime` | Ticket de entrada en barrera | Único por estancia |
| `Ticket` | `id`, `stayId`, `ticketCode`, `totalAmount`, `status` | `UUID`, `BigDecimal`, `TicketStatus` | Ticket de cobro de salida | Relación 1:1 (`uk_stay_id` **IN-20**) |

---

## 3. Endpoints Expuestos y Eventos Consumidos (Fase II)

### API REST:
| Método HTTP | Endpoint | Descripción | Rol Requerido |
|---|---|---|---|
| `POST` | `/v1/entry-tickets` | Emisión de ticket de entrada en barrera | `ADMIN`, `OPERARIO` |
| `GET` | `/v1/entry-tickets/{id}` | Consulta de ticket de entrada | `ADMIN`, `OPERARIO` |
| `POST` | `/v1/tickets` | Generación manual de ticket de salida | `ADMIN`, `OPERARIO` |
| `GET` | `/v1/tickets` | Listado de tickets de cobro | `ADMIN`, `OPERARIO` |
| `GET` | `/v1/tickets/{id}` | Consulta por UUID | `ADMIN`, `OPERARIO` |
| `GET` | `/v1/tickets/code/{code}` | Consulta de ticket por código | `ADMIN`, `OPERARIO`, `USER` |
| `PATCH` | `/v1/tickets/{id}/lost` | Marca de ticket extraviado (**IN-22**) | `ADMIN`, `OPERARIO` |

### Consumo Asíncrono AMQP:
- **Cola Consumida:** `ticket-service-stay-closed-queue`
- **Evento Procesado:** `StayClosedEvent`
- **Acción:** Generar y persistir el `Ticket` de salida de forma atómica e idempotente (**IN-20**).

---

## 4. Arquitectura y Seguridad (Fase II)

- **Adaptadores de Entrada Duales:** Controllers REST (`EntryTicketController`, `TicketController`) y Event Listener (`TicketEventListenerAdapter`).
- **Prevención de Duplicados Concurrentes:** Captura de `DataIntegrityViolationException` en la restricción `uk_stay_id`.
- **Base de Datos:** Postgres dedicado `ticket_db` en puerto `5436` (perfil `prod`), migraciones Flyway `V1__init.sql`.
- **Formato Común de Errores RFC 7807 (SEC-11):** Respuestas estandarizadas `ProblemDetail` / `ErrorResponse`.
