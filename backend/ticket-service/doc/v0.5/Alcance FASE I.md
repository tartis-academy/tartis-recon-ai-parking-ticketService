# ticket-service — Alcance y Especificación de Fase 1 (v0.5.0)

Este documento especifica el alcance funcional, el modelo de datos y los endpoints del microservicio `ticket-service` correspondientes a la **Fase 1 (MVP - v0.5.0)** del sistema **TARTIS Recon-AI**.

---

## 1. Responsabilidad del Microservicio en Fase 1

En la Fase 1 (`v0.5.0`), `ticket-service` actúa como el **módulo síncrono inicial de generación de tickets de salida**:

- **Generación Síncrona de Ticket:** Creación manual de un ticket de salida (`Ticket`) asociado al cierre de una estancia.
- **Consulta de Tickets:** Recuperación de datos de ticket por UUID o listado general.

---

## 2. Modelo de Dominio (`Ticket`) en Fase 1

| Atributo | Tipo Java | Descripción | Obligatorio |
|---|---|---|---|
| `id` | `UUID` | Identificador único del ticket | Sí |
| `stayId` | `UUID` | Identificador de la estancia asociada | Sí |
| `code` | `String` | Código numérico/alfanumérico único del ticket | Sí |
| `amount` | `BigDecimal` | Importe a pagar calculado | Sí |
| `issuedAt` | `LocalDateTime` | Fecha y hora de emisión del ticket | Sí |

---

## 3. Endpoints REST Expuestos en Fase 1 (v0.5.0)

| Método HTTP | Endpoint | Descripción | Respuesta Exitosa |
|---|---|---|---|
| `POST` | `/v1/tickets` | Generación manual/síncrona de ticket de salida | `201 Created` (`TicketResponse`) |
| `GET` | `/v1/tickets` | Listado general de tickets emitidos | `200 OK` (`List<TicketResponse>`) |
| `GET` | `/v1/tickets/{id}` | Consulta de ticket por UUID | `200 OK` (`TicketResponse`) |

---

## 4. Persistencia PostgreSQL — Baseline (`V1__init.sql`)

```sql
CREATE SCHEMA IF NOT EXISTS ticket;

CREATE TABLE ticket.tickets (
    id UUID PRIMARY KEY,
    stay_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL CONSTRAINT uk_ticket_code UNIQUE,
    amount NUMERIC(10,2) NOT NULL,
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

---

## 5. Exclusiones de la Fase 1 (Diferencias con Fase 2 / v1.0.0)

- ❌ **Sin consumo asíncrono de RabbitMQ:** En Fase 1 no existía `TicketEventListenerAdapter` ni la cola `ticket-service-stay-closed-queue`.
- ❌ **Sin tickets de entrada (`EntryTicket`):** No existía la entidad ni los controladores `/v1/entry-tickets`.
- ❌ **Sin gestión de tickets perdidos (IN-22):** Sin endpoint `PATCH /v1/tickets/{id}/lost`.
- ❌ **Sin restricción de unicidad 1:1 estancia-ticket (IN-20):** Sin restricción `uk_stay_id` en base de datos.
- ❌ **Sin Dead Letter Queue (DLQ):** Sin cola `ticket-service-stay-closed-dlq`.
- ❌ **Sin autenticación Keycloak ni RBAC (SEC-03).**
- ❌ **Sin Kong API Gateway.**
