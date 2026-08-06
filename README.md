# tartis-recon-ai-parking — ticket-service

## Responsabilidad del microservicio

`ticket-service` es el microservicio encargado de la gestión, emisión y almacenamiento de los **Tickets del sistema de parking**, abarcando dos entidades y dominios bien diferenciados:

1. **Tickets de Entrada (`EntryTicket`):**
   - Emitidos en el momento del registro de entrada (check-in) de un vehículo.
   - Almacena el código alfanumérico único del ticket de entrada, la fecha/hora de entrada y la vinculación con la estancia (`stayId`).

2. **Tickets de Salida / Cobro (`Ticket`):**
   - Emitidos al finalizar la estancia (check-out) para registrar el importe total a cobrar y el resumen del cierre.
   - Mantiene la relación de dominio **IN-20** (relación 1:1 entre una estancia `stayId` y su `Ticket` final de cobro).
   - Soporta generación síncrona vía REST (`POST /v1/tickets`) y asíncrona mediante el consumo de eventos `StayClosedEvent` desde RabbitMQ.

## Endpoints expuestos

Todos los endpoints requieren autenticación mediante Bearer Token (Access Token emitido por Keycloak), exceptuando el probe público de salud.

### Endpoints de Tickets de Entrada (`/v1/entry-tickets`)

| Método | Endpoint | Descripción | Roles Autorizados |
|---|---|---|---|
| `POST` | `/v1/entry-tickets` | Crea un nuevo ticket de entrada asociado a un check-in | `ADMIN`, `OPERARIO` |
| `GET` | `/v1/entry-tickets` | Obtiene el listado completo de tickets de entrada | `ADMIN` |
| `GET` | `/v1/entry-tickets/{id}/code` | Consulta un ticket de entrada por su UUID | `USER`, `ADMIN`, `OPERARIO` |
| `PUT` | `/v1/entry-tickets/{id}` | Actualiza los datos de un ticket de entrada existente | `ADMIN` |

### Endpoints de Tickets de Salida / Cobro (`/v1/tickets`)

| Método | Endpoint | Descripción | Roles Autorizados |
|---|---|---|---|
| `POST` | `/v1/tickets` | Genera un ticket de salida/cobro (relación 1:1 IN-20 con `stayId`) | `ADMIN`, `OPERARIO` |
| `GET` | `/v1/tickets` | Listado paginado de tickets de salida (búsqueda por `stayId` o término) | `ADMIN` |
| `GET` | `/v1/tickets/{id}` | Obtiene el detalle de un ticket de salida por su UUID | `USER`, `ADMIN`, `OPERARIO` |
| `GET` | `/actuator/health` | Probes de salud del servicio (Liveness / Readiness) | Público |

## Eventos publicados y consumidos

Este microservicio combina recepción de llamadas REST síncronas con consumo de eventos de dominio asíncronos.

- **Eventos publicados en RabbitMQ:** Ninguno.
- **Eventos consumidos de RabbitMQ:**
  - **`StayClosedEvent`:** Escucha en la cola `ticket-service-stay-closed-queue` (Exchange `stay.events`, routing key `stay.closed`). Al finalizar un check-out en `stay-service`, este servicio procesa el evento y genera de forma asíncrona el ticket de cobro.
  - **Idempotencia:** Protección contra duplicados mediante verificación previa (`TicketAlreadyExistsException`) y restricción de unicidad en BD (`uk_stay_id`). Si el evento reaparece, se emite un log de advertencia y se confirma la recepción (ACK).
  - **Resiliencia & Dead Letter Queue (DLQ):** 6 reintentos con backoff exponencial. Tras agotar los reintentos, el mensaje no procesable se envía a la cola muerta `ticket-service-stay-closed-dlq`.

## Variables de entorno

| Variable | Descripción | Valor por defecto (Dev) | Perfil / Uso |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Perfil activo de Spring Boot | `dev` | `dev` / `prod` |
| `DB_HOST` | Host de la BD compartida de desarrollo | `localhost` | Dev |
| `DB_PORT` | Puerto de la BD compartida | `5432` | Dev |
| `DB_NAME` | Nombre de la BD de desarrollo | `parking_dev` | Dev |
| `DB_USER` | Usuario de la BD de desarrollo | `parking_dev` | Dev |
| `DB_PASSWORD` | Contraseña de la BD de desarrollo | `change.me` | Dev |
| `TICKET_DB_HOST` | Host de la BD dedicada de tickets | — | Prod / Aislado |
| `TICKET_DB_PORT` | Puerto de la BD dedicada de tickets | `5432` | Prod / Aislado |
| `TICKET_DB_NAME` | Nombre de la BD dedicada | `ticket_db` | Prod / Aislado |
| `TICKET_DB_USER` | Usuario de la BD dedicada | — | Prod / Aislado |
| `TICKET_DB_PASSWORD` | Contraseña de la BD dedicada | — | Prod / Aislado |
| `RABBITMQ_HOST` | Host del broker RabbitMQ | `rabbitmq` | Dev / Prod |
| `RABBITMQ_USER` | Usuario de autenticación RabbitMQ | `guest` | Dev / Prod |
| `RABBITMQ_PASSWORD` | Contraseña de autenticación RabbitMQ | `guest` | Dev / Prod |
| `KEYCLOAK_ISSUER_URI` | URI del emisor de Keycloak (Issuer URI) | `http://localhost:8180/realms/parking` | Dev / Prod |

## Ejecución de forma aislada

Para ejecutar y probar `ticket-service` de forma independiente sin depender del resto de microservicios:

1. **Opción 1: Entorno de Desarrollo (Perfil `dev`)**
   Navegar a la carpeta del microservicio y arrancar con Maven:
   ```bash
   cd backend/ticket-service
   mvn spring-boot:run
   ```
   *El servicio se conectará al esquema `ticket` del Postgres compartido.*

2. **Opción 2: Base de Datos Dedicada (Perfil `prod` / Contenedores Aislados)**
   Para ejecutar contra una base de datos PostgreSQL exclusiva en puerto `5436`:
   ```bash
   cd backend/ticket-service
   cp .env.example .env
   docker compose up -d
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

## Migraciones de base de datos (Flyway)

El esquema ya no se crea a mano ni con un `schema.sql` montado como init
script: `V1__init.sql` (en `backend/ticket-service/src/main/resources/db/migration`)
es la baseline, y Flyway la aplica solo al arrancar la app contra la BD
dedicada (perfil `prod`). En dev, Flyway está desactivado
(`spring.flyway.enabled=false` en `application-dev.properties`): el Postgres
compartido con 5 schemas sigue gestionado por `ddl-auto=update`, fuera del
alcance de esta migración.

Para añadir un cambio de esquema: crea `V2__descripcion.sql` (nunca edites
`V1__init.sql` una vez desplegado) en la misma carpeta, con el DDL nuevo.
Flyway lo detecta y lo aplica en el siguiente arranque.