package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TicketResponse(UUID uniqueId, UUID stayId, Instant issuedAt, BigDecimal totalAmount) {
}

