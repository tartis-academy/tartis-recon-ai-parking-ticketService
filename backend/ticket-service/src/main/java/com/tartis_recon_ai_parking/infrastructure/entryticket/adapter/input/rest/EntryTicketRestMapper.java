package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.request.EntryTicketRequest;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.response.EntryTicketResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EntryTicketRestMapper {

    EntryTicketCreateDTO toCreateDTO(EntryTicketRequest request);

    @Mapping(source = "uniqueId", target = "id")
    EntryTicketResponse toResponse(EntryTicketDTO entryTicket);

    Iterable<EntryTicketResponse> toResponseList(Iterable<EntryTicketDTO> entryTicket);
}