package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener.dto;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EntryTicketOfflineEventDto(
        UUID stayId,
        String plate,
        String offlineCode,
        Instant issuedAt
) {}
