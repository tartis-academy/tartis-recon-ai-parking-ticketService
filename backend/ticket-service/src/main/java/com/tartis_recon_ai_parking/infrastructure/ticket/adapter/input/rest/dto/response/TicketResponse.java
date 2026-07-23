package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TicketResponse {
    public UUID uniqueId;
    public UUID stayId;
    public Instant issuedAt;
    public BigDecimal totalAmount;
}