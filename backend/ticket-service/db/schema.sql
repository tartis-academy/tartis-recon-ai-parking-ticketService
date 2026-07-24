-- DDL para el perfil prod (ddl-auto=validate): sin Flyway/Liquibase todavia,
-- el esquema se crea fuera de banda. Debe reflejar exactamente EntryTicketEntity
-- y TicketEntity (tabla "receipts"). Se monta como init script en la Postgres
-- dedicada de ticket-service.

CREATE TABLE IF NOT EXISTS entry_ticket (
    id        UUID PRIMARY KEY,
    stay_id   UUID NOT NULL UNIQUE,
    code      VARCHAR(32) NOT NULL UNIQUE,
    issued_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS receipts (
    unique_id    UUID PRIMARY KEY,
    stay_id      UUID NOT NULL,
    issued_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    total_amount NUMERIC(10,2) NOT NULL
);
