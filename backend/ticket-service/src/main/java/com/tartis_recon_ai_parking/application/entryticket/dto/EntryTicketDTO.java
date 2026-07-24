package com.tartis_recon_ai_parking.application.entryticket.dto;


import java.time.Instant;
import java.util.UUID;

public record EntryTicketDTO(
    UUID uniqueId,
    UUID stayId,
    Instant issuedAt,
    String code) {
}
