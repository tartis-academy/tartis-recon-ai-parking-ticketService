package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.request.EntryTicketRequest;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.response.EntryTicketResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntryTicketRestMapper {

    EntryTicketCreateDTO toCreateDTO(EntryTicketRequest request);

    EntryTicketResponse toResponse(EntryTicketDTO entryTicket);

    Iterable<EntryTicketResponse> toResponseList(Iterable<EntryTicketDTO> entryTicket);
}