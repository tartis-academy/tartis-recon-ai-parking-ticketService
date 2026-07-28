package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record EntryTicketRequest(
    @NotNull(message = "El stayId no puede ser nulo")
    UUID stayId
) {}
