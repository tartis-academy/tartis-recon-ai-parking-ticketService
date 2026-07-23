package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.request.TicketRequest;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response.TicketResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TicketRestMapper {

    @Mapping(target = "issuedAt", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    TicketCreateDTO toCreateDTO(TicketRequest request);

    TicketResponse toResponse(TicketDTO ticket);

    List<TicketResponse> toResponseList(Iterable<TicketDTO> tickets);
}

