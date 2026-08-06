# ticket-service — Alcance y Especificación de Fase 2 (v1.0.0)

Este documento especifica el alcance funcional completo, los componentes asíncronos y la seguridad del microservicio `ticket-service` correspondientes a la **Fase 2 (v1.0.0)** del sistema **TARTIS Recon-AI**.

---

## 1. Responsabilidad del Microservicio en Fase 2

En la Fase 2 (`v1.0.0`), `ticket-service` evoluiona incorporando la **emisión asíncrona por eventos** y la gestión completa de tickets de entrada y cobro:

- **Consumo Asíncrono de `StayClosedEvent`:** `TicketEventListenerAdapter` (`@RabbitListener`) escuchando en la cola `ticket-service-stay-closed-queue` para emitir automáticamente el ticket de salida tras el check-out en `stay-service`.
- **Garantía de Idempotencia & Unicidad 1:1 (IN-20):** Restricción de unicidad `uk_stay_id` en base de datos y tratamiento de `TicketAlreadyExistsException` permitiendo ignorar eventos duplicados sin fallo de cola.
- **Resiliencia & Dead Letter Queue (DLQ):** Reintentos exponenciales y desvío a `ticket-service-stay-closed-dlq`.
- **Gestión de Tickets de Entrada (`EntryTicket`):** Entidad y controladores REST (`/v1/entry-tickets`) para tickets emitidos en barrera durante el check-in.
- **Gestión de Tickets Perdidos (IN-22):** Endpoint `PATCH /v1/tickets/{ticketId}/lost` para declarar tickets extraviados.
- **Timeout Pesimista HikariCP:** Configuración de `lock_timeout = 2000` ms para prevenir bloqueos en PostgreSQL.
- **Seguridad & RBAC (SEC-03):** Integración con Keycloak IdP y Kong API Gateway (`ADMIN`/`OPERARIO` para emisión y consultas; `USER` para lectura por código de barras).

---

## 2. Endpoints REST & Seguridad RBAC (v1.0.0)

| Método HTTP | Endpoint | Descripción | Roles Permitidos | Respuesta Exitosa |
|---|---|---|---|---|
| `POST` | `/v1/entry-tickets` | Creación de ticket de entrada en barrera | `ADMIN`, `OPERARIO` | `201 Created` |
| `GET` | `/v1/entry-tickets/{id}` | Consulta de ticket de entrada | `ADMIN`, `OPERARIO` | `200 OK` |
| `POST` | `/v1/tickets` | Generación de ticket de salida | `ADMIN`, `OPERARIO` | `201 Created` |
| `GET` | `/v1/tickets` | Listado general de tickets | `ADMIN`, `OPERARIO` | `200 OK` |
| `GET` | `/v1/tickets/{id}` | Consulta por UUID | `ADMIN`, `OPERARIO` | `200 OK` |
| `GET` | `/v1/tickets/code/{code}` | Consulta por código de barras | `ADMIN`, `OPERARIO`, `USER` | `200 OK` |
| `PATCH` | `/v1/tickets/{ticketId}/lost` | **Marcado de ticket perdido (IN-22)** | `ADMIN`, `OPERARIO` | `200 OK` |

---

## 3. Persistencia PostgreSQL (Migraciones Flyway)

### `V1__init.sql` (Baseline) & `V2__add_entry_tickets_and_uk_stay_id.sql`
```sql
ALTER TABLE ticket.tickets ADD CONSTRAINT uk_stay_id UNIQUE (stay_id);
```
En entorno `prod`, se ejecuta sobre base de datos dedicada `ticket_db` en puerto `5436`.
