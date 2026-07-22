package com.tartis_recon_ai_parking.application.ticket.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TicketCreateDTO (UUID stayId,Instant issuedAt, BigDecimal totalAmount){
}
