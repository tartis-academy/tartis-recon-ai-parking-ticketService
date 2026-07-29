package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener.dto;

import java.time.Instant;
import java.util.UUID;

public record StayClosedEvent(
        UUID eventId,
        String eventType,
        String version,
        Instant timestamp,
        StayClosedEventData data
) {
}
