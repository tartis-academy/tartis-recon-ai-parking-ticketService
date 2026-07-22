package com.tartis_recon_ai_parking.application.ticket.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TicketDTO (
    UUID uniqueId,
    UUID stayId,
    Instant issuedAt,
    BigDecimal totalAmount)
    {}
