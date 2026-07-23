package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// Representa la tabla "receipts" en el sistema de base de datos relacional.
@Entity
@Table(name = "receipts")
public class TicketEntity{

    // clave primaria (PK): generada por el dominio (Ticket.uniqueId), no por la BD.
    @Id
    private UUID uniqueId;

    @Column(nullable = false)
    private UUID stayId;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;


    public TicketEntity() {}

    public UUID getUniqueId() { return uniqueId; }
    public void setUniqueId(UUID uniqueId) { this.uniqueId = uniqueId; }

    public UUID getStayId() { return stayId; }
    public void setStayId(UUID stayId) { this.stayId = stayId; }

    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}