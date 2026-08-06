# Alcance de la Fase I (MVP v0.5.0) — ticket-service

Documento explicativo del alcance, responsabilidad, modelo de dominio, endpoints expuestos e infraestructura del microservicio `ticket-service` durante la **Fase I (MVP v0.5.0)** del sistema de parking inteligente **TARTIS Recon-AI**.

---

## 1. Responsabilidad del Microservicio en Fase I

En la Fase I, `ticket-service` se encarga exclusivamente de la generación y consulta síncrona de tickets de salida/cobro:
- **Generación de Ticket de Salida (`Ticket`):** Creación del comprobante de cobro emitido síncronamente al completar una estancia.
- **Consulta de Tickets:** Consulta individual o listado de tickets registrados en el sistema.

---

## 2. Modelo de Dominio (Fase I)

Entidad principal **`Ticket`** con los siguientes atributos:

| Atributo | Tipo | Descripción | Validación / Restricción |
|---|---|---|---|
| `id` | `UUID` | Identificador único universal del ticket | Autogenerado (PK) |
| `stayId` | `UUID` | Identificador de la estancia asociada | No nulo |
| `ticketCode` | `String` | Código de barras / alfanumérico del ticket | Único, no nulo |
| `issueTime` | `LocalDateTime` | Fecha y hora de emisión | No nulo |
| `totalAmount` | `BigDecimal` | Importe total cobrado | No nulo $\ge 0$ |
| `status` | `TicketStatus` | Estado del ticket (`ISSUED`, `PAID`) | No nulo |

---

## 3. Endpoints REST Expuestos (Fase I)

| Método HTTP | Endpoint | Descripción | Cuerpo / Parámetros | Respuesta Éxito |
|---|---|---|---|---|
| `POST` | `/v1/tickets` | Generación síncrona de ticket de cobro | JSON `CreateTicketRequest` | `201 Created` (`TicketResponse`) |
| `GET` | `/v1/tickets` | Listado de tickets de cobro emitidos | Ninguno | `200 OK` (Lista de `TicketResponse`) |
| `GET` | `/v1/tickets/{id}` | Consulta de ticket por su UUID | `{id}` (UUID) | `200 OK` (`TicketResponse`) |

---

## 4. Arquitectura y Persistencia en Fase I

- **Arquitectura Hexagonal:** Adaptador de entrada REST (`TicketRestControllerAdapter`), Casos de Uso (`CreateTicketUseCase`, `GetTicketUseCase`), Adaptador de salida JPA (`TicketPersistenceAdapter`).
- **Base de Datos:** PostgreSQL compartido `parking_dev` en puerto `5432`, esquema `ticket`.

---

## 5. Diferencias Clave respecto a la Fase II (v1.0.0)

1. **Tickets de Entrada (`EntryTicket`):** No existe el dominio ni endpoints (`/v1/entry-tickets`) para la emisión de tickets en barrera al entrar.
2. **Consumo Asíncrono por Eventos:** No existe el consumidor `@RabbitListener` de `StayClosedEvent`.
3. **Gestión de Tickets Perdidos (IN-22):** No existe la funcionalidad `PATCH /v1/tickets/{ticketId}/lost`.
4. **Idempotencia & Unicidad en BD (IN-20):** No existe la restricción de base de datos `uk_stay_id` ni `TicketAlreadyExistsException`.
5. **Resiliencia DLQ:** No existen colas de mensajes muertos (`ticket-service-stay-closed-dlq`).
6. **Seguridad OAuth2 / Keycloak & Kong:** Sin verificación de tokens JWT ni roles RBAC.
