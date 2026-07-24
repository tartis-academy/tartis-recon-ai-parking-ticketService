package com.tartis_recon_ai_parking.application.ticket.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class TicketDTO {

    private final UUID id;
    private final UUID stayId;
    private final Instant issuedAt;
    private final BigDecimal totalAmount;
}
