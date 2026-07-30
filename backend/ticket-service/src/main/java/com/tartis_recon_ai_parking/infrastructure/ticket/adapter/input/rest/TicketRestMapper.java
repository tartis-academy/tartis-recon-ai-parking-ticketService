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

    // TicketRequest trae stayId y opcionalmente totalAmount (si es síncrono).
    default TicketCreateDTO toCreateDTO(TicketRequest request) {
        BigDecimal amount = request.getTotalAmount() != null ? request.getTotalAmount() : BigDecimal.ZERO;
        return new TicketCreateDTO(request.getStayId(), Instant.now(), amount);
    }

    TicketResponse toResponse(TicketDTO ticket);

    List<TicketResponse> toResponseList(Iterable<TicketDTO> tickets);
}

