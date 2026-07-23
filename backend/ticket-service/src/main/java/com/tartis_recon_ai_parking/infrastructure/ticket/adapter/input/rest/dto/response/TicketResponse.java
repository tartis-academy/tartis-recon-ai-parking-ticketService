package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TicketResponse {
    private final UUID id;
    private final UUID stayId;
    private final Instant issuedAt;
    private final BigDecimal totalAmount;

    public TicketResponse(UUID id, UUID stayId,Instant issuedAt,BigDecimal totalAmount){

        this.id=id;
        this.issuedAt=issuedAt;
        this.stayId=stayId;
        this.totalAmount=totalAmount;
    }

      public UUID getId() {
        return id;
    }

    public UUID stayId() {
        return stayId;
    }

    public Instant getissuedAt() {
        return issuedAt;
    }

    public BigDecimal getTotalAmount(){
        return totalAmount;
    }
}
