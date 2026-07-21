package com.tartis_recon_ai_parking.application.entryticket.dto;

import com.tartis_recon_ai_parking.domain.entryticket.EntryTicketStatus;
import java.time.Instant;
import java.util.UUID;

public record EntryTicketDTO(UUID id, UUID stayId, String code, Instant issuedAt, EntryTicketStatus status) {}
