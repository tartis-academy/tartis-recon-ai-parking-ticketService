package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.request;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TicketRequest(UUID stayId, Instant issuedAt, BigDecimal totalAmount) {
}

