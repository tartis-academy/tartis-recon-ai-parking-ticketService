# tartis-recon-ai-parking — ticket-service

## 1. Responsabilidad del microservicio

`ticket-service` es el microservicio encargado de la emisión, gestión y almacenamiento de los **Tickets del sistema de parking** en el ecosistema **TARTIS Recon-AI**, abarcando dos entidades y dominios bien diferenciados:

1. **Tickets de Entrada (`EntryTicket`):**
   - Emitidos síncronamente durante el check-in de un vehículo.
   - Registra el código de barras único del ticket, la fecha y hora de entrada y la vinculación con la estancia (`stayId`).

2. **Tickets de Salida / Cobro (`Ticket`):**
   - Emitidos al finalizar la estancia para registrar el importe total cobrado.
   - Mantiene la relación de dominio **IN-20** (relación 1:1 entre una estancia `stayId` y su `Ticket` final de cobro).
   - Soporta generación síncrona vía REST (`POST /v1/tickets`) y asíncrona mediante el consumo de eventos `StayClosedEvent` desde RabbitMQ.
   - **Gestión de Tickets Perdidos (IN-22):** Endpoint `PATCH /v1/tickets/{ticketId}/lost` para marcar un ticket como extraviado o perdido.
   - **Publicación de Eventos:** Emisión del evento `TicketChangedEvent` tras modificaciones o emisión de tickets.

---

## 2. Endpoints expuestos

Todos los endpoints requieren autenticación mediante Bearer Access Token (emitido por Keycloak), exceptuando las sondas públicas de salud.

### Endpoints de Tickets de Entrada (`/v1/entry-tickets`)

| Método | Endpoint | Descripción | Roles Autorizados (RBAC SEC-03) | Respuesta Exitosa |
|---|---|---|---|---|
| `POST` | `/v1/entry-tickets` | Crea un nuevo ticket de entrada asociado a un check-in | `ADMIN`, `OPERARIO` | `201 Created` (`EntryTicketResponse`) |
| `GET` | `/v1/entry-tickets` | Obtiene el listado completo de tickets de entrada | `ADMIN` | `200 OK` (`List<EntryTicketResponse>`) |
| `GET` | `/v1/entry-tickets/{id}/code` | Consulta un ticket de entrada por su UUID | `USER`, `ADMIN`, `OPERARIO` | `200 OK` (`EntryTicketResponse`) |
| `PUT` | `/v1/entry-tickets/{id}` | Actualiza los datos de un ticket de entrada existente | `ADMIN` | `200 OK` (`EntryTicketResponse`) |

### Endpoints de Tickets de Salida / Cobro (`/v1/tickets`)

| Método | Endpoint | Descripción | Roles Autorizados (RBAC SEC-03) | Respuesta Exitosa |
|---|---|---|---|---|
| `POST` | `/v1/tickets` | Genera síncronamente un ticket de salida/cobro (relación 1:1 IN-20) | `ADMIN`, `OPERARIO` | `201 Created` (`TicketResponse`) |
| `GET` | `/v1/tickets` | Listado paginado de tickets de salida | `ADMIN` | `200 OK` (`List<TicketResponse>`) |
| `GET` | `/v1/tickets/{id}` | Obtiene el detalle de un ticket de salida por su UUID | `USER`, `ADMIN`, `OPERARIO` | `200 OK` (`TicketResponse`) |
| `PATCH` | `/v1/tickets/{ticketId}/lost` | Marca un ticket de salida como extraviado/perdido (IN-22) | `ADMIN`, `OPERARIO` | `200 OK` (`TicketResponse`) |
| `GET` | `/actuator/health` | Probes de salud del servicio (Liveness / Readiness) | Público | `200 OK` |

---

## 3. Casos de Uso (Arquitectura Hexagonal)

### Dominios y Casos de Uso:
- **`CreateEntryTicketUseCase`:** Genera un ticket de entrada asignando su código alfanumérico único.
- **`GetEntryTicketByCodeUseCase` / `GetEntryTicketUseCase`:** Consulta tickets de entrada por código de barras o UUID.
- **`ListEntryTicketsUseCase` / `UpdateEntryTicketUseCase`:** Gestión y actualización de tickets de entrada.
- **`CreateTicketUseCase`:** Crea un ticket de cobro final verificando que no exista uno previo para la misma estancia (IN-20).
- **`GetTicketByCodeUseCase` / `GetTicketUseCase`:** Búsqueda de tickets de cobro.
- **`ListTicketsUseCase`:** Recuperación de listados de tickets de salida.
- **`MarkTicketAsLostUseCase`:** Aplica la marca de ticket extraviado (IN-22).

### Puertos de Dominio:
- **Puertos de Entrada:** REST API (`EntryTicketRestAdapter`, `TicketRestAdapter`), AMQP Listener (`TicketEventListenerAdapter`).
- **Puertos de Salida:** `EntryTicketPersistence`, `TicketPersistence`, `TicketEventPublisher` (`TicketEventPublisherAdapter`).

---

## 4. Eventos publicados y consumidos

### Eventos Publicados en RabbitMQ:
- **`TicketChangedEvent`:** Emite eventos de notificación a RabbitMQ tras la creación o actualización de tickets (`TicketEventPublisherAdapter` / `TicketChangedEventRelay`).

### Eventos Consumidos de RabbitMQ:
- **`StayClosedEvent`:** Escucha en la cola `ticket-service-stay-closed-queue` (Exchange `stay.events`, routing key `stay.closed`). Al recibir un evento de cierre de estancia, genera automáticamente el ticket de salida.
- **Idempotencia (IN-20):** Si el ticket ya existe (`TicketAlreadyExistsException` o violación de restricción `uk_stay_id` en BD), el evento se confirma (`ACK`) registrando un aviso en el log para no duplicar tickets ni bloquear la cola.
- **Resiliencia & Dead Letter Queue (DLQ):** 6 reintentos exponenciales. Los mensajes fallidos se desvían a la cola `ticket-service-stay-closed-dlq`.

---

## 5. Variables de entorno

| Variable | Descripción | Valor por defecto (Dev) | Perfil / Uso |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Perfil activo de Spring Boot | `dev` | `dev` / `prod` |
| `SERVER_PORT` | Puerto HTTP del servicio | `8084` | Dev / Prod |
| `DB_HOST` | Host de la BD compartida de desarrollo | `localhost` | Dev |
| `DB_PORT` | Puerto de la BD compartida | `5432` | Dev |
| `DB_NAME` | Nombre de la BD de desarrollo | `parking_dev` | Dev |
| `TICKET_DB_HOST` | Host de la BD dedicada de tickets | `parking-ticket-postgres` | Prod / Aislado |
| `TICKET_DB_PORT` | Puerto del host para la BD dedicada | `5436` (externo) / `5432` (interno) | Prod / Aislado |
| `TICKET_DB_NAME` | Nombre de la BD dedicada | `ticket_db` | Prod / Aislado |
| `TICKET_DB_USER` | Usuario de la BD dedicada | `ticket_user` | Prod / Aislado |
| `TICKET_DB_PASSWORD` | Contraseña de la BD dedicada | `ticket_pass` | Prod / Aislado |
| `RABBITMQ_HOST` | Host del broker RabbitMQ | `rabbitmq` | Dev / Prod |
| `RABBITMQ_USER` | Usuario de autenticación RabbitMQ | `guest` | Dev / Prod |
| `RABBITMQ_PASSWORD` | Contraseña de autenticación RabbitMQ | `guest` | Dev / Prod |
| `KEYCLOAK_ISSUER_URI` | URI del emisor de Keycloak | `http://localhost:8180/realms/parking` | Dev / Prod |

---

## 6. Ejecución de forma aislada

### Opción 1: Entorno de Desarrollo (Perfil `dev`)
```bash
cd backend/ticket-service
mvn spring-boot:run
```

### Opción 2: Base de Datos Dedicada (Perfil `prod` / Contenedores Aislados)
1. Arrancar la base de datos PostgreSQL en el puerto `5436`:
   ```bash
   cd backend/ticket-service
   cp .env.example .env
   docker compose up -d
   ```
2. Ejecutar la aplicación Spring Boot activando el perfil `prod` para aplicar migraciones Flyway (`V1__init.sql`, `V2__add_entry_tickets_and_uk_stay_id.sql`):
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```