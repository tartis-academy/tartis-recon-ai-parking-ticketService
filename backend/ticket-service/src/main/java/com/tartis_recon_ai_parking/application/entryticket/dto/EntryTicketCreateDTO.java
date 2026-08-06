package com.tartis_recon_ai_parking.application.entryticket.dto;


import java.time.Instant;
import java.util.UUID;

public record EntryTicketCreateDTO(
    UUID stayId,
    String code,       // null si es online, presente si es offline
    Instant issuedAt   // null si es online, presente si es offline
) {
    /**
     * Constructor de conveniencia para la emisión online estándar.
     * Mantiene compatibilidad con el código previo donde solo se enviaba stayId.
     */
    public EntryTicketCreateDTO(UUID stayId) {
        this(stayId, null, null);
    }
}
