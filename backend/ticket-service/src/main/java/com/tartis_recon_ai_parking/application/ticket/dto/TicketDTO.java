package com.tartis_recon_ai_parking.application.ticket.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TicketDTO { 
    private final UUID id;
    private final UUID stayId;
    private final Instant issuedAt;
    private final BigDecimal totalAmount;


public TicketDTO(UUID id, UUID stayId, Instant issuedAt,BigDecimal totalAmount) {
        this.id = id;
        this.stayId = stayId;
        this.issuedAt = issuedAt;
        this.totalAmount = totalAmount;
    }

     public UUID getId() {
        return id;
    }

    public UUID getStayId() {
        return stayId;
    }

    public Instant getissuedAt() {
        return issuedAt;
    }

    public BigDecimal getTotalAmount(){
        return totalAmount;
    }
}



   
    
