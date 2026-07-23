package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketRequest {

    @NotNull(message = "El stayId no puede ser nulo")
    private UUID stayId;
}