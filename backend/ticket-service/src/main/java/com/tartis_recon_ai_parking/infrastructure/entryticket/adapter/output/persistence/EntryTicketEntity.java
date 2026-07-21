package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "entry_ticket")
public class EntryTicketEntity {

    @Id
    private UUID id;

    // IN-20: relacion 1:1 con la estancia
    @Column(nullable = false, unique = true)
    private UUID stayId;

    // codigo impreso en el barcode: unico e indexado, se busca por el en la salida
    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(nullable = false)
    private Instant issuedAt;

    private Instant usedAt; // nullable: solo se rellena al usarse (IN-21)

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getStayId() { return stayId; }
    public void setStayId(UUID stayId) { this.stayId = stayId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }

    public Instant getUsedAt() { return usedAt; }
    public void setUsedAt(Instant usedAt) { this.usedAt = usedAt; }
}