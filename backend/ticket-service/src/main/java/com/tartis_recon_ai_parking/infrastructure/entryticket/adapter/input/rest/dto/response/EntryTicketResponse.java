package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.response;

import java.time.Instant;
import java.util.UUID;

public record EntryTicketResponse(UUID id, UUID stayId, Instant issuedAt, String code) {}
