package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StayClosedEventData(
        UUID stayId,
        String plate,
        String spotCode,
        Instant entryDate,
        Instant exitDate,
        BigDecimal totalAmount
) {
}
