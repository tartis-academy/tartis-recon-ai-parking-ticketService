package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class TicketResponse {

    private UUID uniqueId;
    private UUID stayId;
    private Instant issuedAt;
    private BigDecimal totalAmount;
}
