package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.request.TicketRequest;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response.TicketResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TicketRestMapper {

    TicketCreateDTO toCreateDTO(TicketRequest request);

    TicketResponse toResponse(TicketDTO ticketDTO);

    Iterable<TicketResponse> toResponseList(Iterable<TicketDTO> tickets);
}

