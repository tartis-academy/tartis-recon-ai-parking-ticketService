package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.request.TicketRequest;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response.TicketResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketRestMapper {

    // TicketRequest solo trae stayId (el contrato aun no transporta el importe
    // calculado por stay-service): issuedAt se fija a "ahora" y totalAmount
    // queda en 0 como placeholder, igual que el resto del calculo de tarifa
    // real esta stubbeado en el flujo de checkout.
    default TicketCreateDTO toCreateDTO(TicketRequest request) {
        return new TicketCreateDTO(request.getStayId(), Instant.now(), BigDecimal.ZERO);
    }

    TicketResponse toResponse(TicketDTO ticket);

    List<TicketResponse> toResponseList(Iterable<TicketDTO> tickets);
}

