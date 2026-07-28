-- RECON-812: migracion baseline de Flyway, sustituye al schema.sql que se
-- montaba como init script de Postgres. Debe reflejar exactamente
-- EntryTicketEntity y TicketEntity (tabla "receipts").
CREATE TABLE entry_ticket (
    id        UUID PRIMARY KEY,
    stay_id   UUID NOT NULL UNIQUE,
    code      VARCHAR(32) NOT NULL UNIQUE,
    issued_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE receipts (
    unique_id    UUID PRIMARY KEY,
    stay_id      UUID NOT NULL,
    issued_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    total_amount NUMERIC(10,2) NOT NULL
);
