package com.tartis_recon_ai_parking.domain.ticket;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;

public class Ticket {

    private UUID uniqueId;
    private UUID stayId;
    private Instant issuedAt;
    private BigDecimal totalAmount;
    
    public Ticket() {
    }

    // Constructor completo
    public Ticket(UUID uniqueId, UUID stayId, Instant issuedAt, BigDecimal totalAmount) {
        this.uniqueId = uniqueId;
        this.stayId = stayId;
        this.issuedAt = issuedAt;
        this.totalAmount = totalAmount;
    }

    private void validateData(UUID uniqueId, UUID stayId, Instant issuedAt, BigDecimal totalAmount){

        if(uniqueId == null) throw new InvalidTicketException("uniqueId is null");
        if(stayId == null) throw new InvalidTicketException("stayId is null");
        if(issuedAt == null) throw new InvalidTicketException("issuedAt is null");
        if(totalAmount == null) throw new InvalidTicketException("totalAmount is null");
   
    }

    public Ticket create(Instant issuedAt, BigDecimal totalAmount){
        UUID uniqueId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        validateData(uniqueId, stayId, issuedAt, totalAmount);
        return new Ticket(uniqueId, stayId, issuedAt, totalAmount);
    }

    public Ticket recreate(UUID uniqueId, UUID stayId, Instant issuedAt, BigDecimal totalAmount){
         validateData(uniqueId, stayId, issuedAt, totalAmount);
        return new Ticket(uniqueId, stayId , issuedAt, totalAmount);
    }

    // --- GETTERS Y SETTERS ---

    public UUID getUniqueId() {
        return uniqueId;
    }


    public UUID getStayId() {
        return stayId;
    }


    public Instant getIssuedAt() {
        return issuedAt;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

}
