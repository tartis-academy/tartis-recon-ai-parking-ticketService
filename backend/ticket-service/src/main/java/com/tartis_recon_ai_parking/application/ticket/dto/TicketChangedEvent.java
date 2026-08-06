package com.tartis_recon_ai_parking.application.ticket.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.domain.ticket.Ticket;

public record TicketChangedEvent(
    UUID eventId,
    String type,
    String version,
    Instant occurredAt,
    TicketChangedData data
) {
    public record TicketChangedData(
        UUID ticketId,
        UUID stayId,
        String code,
        Instant issuedAt,
        String status,
        BigDecimal amount
    ) {}

    public static TicketChangedEvent of(EntryTicket ticket, String status, Instant occurredAt) {
        return new TicketChangedEvent(
            UUID.randomUUID(),
            "TicketChangedEvent",
            "v1",
            occurredAt,
            new TicketChangedData(
                ticket.getUniqueId(),
                ticket.getStayId(),
                ticket.getCode(),
                ticket.getIssuedAt(),
                status != null ? status : "CREATED",
                null
            )
        );
    }

    public static TicketChangedEvent of(EntryTicket ticket, Instant occurredAt) {
        return of(ticket, "CREATED", occurredAt);
    }

    public static TicketChangedEvent of(Ticket receipt, Instant occurredAt) {
        return new TicketChangedEvent(
            UUID.randomUUID(),
            "TicketChangedEvent",
            "v1",
            occurredAt,
            new TicketChangedData(
                receipt.getUniqueId(),
                receipt.getStayId(),
                null,
                receipt.getIssuedAt(),
                "PAID",
                receipt.getTotalAmount()
            )
        );
    }
}
