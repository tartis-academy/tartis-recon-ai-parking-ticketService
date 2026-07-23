package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.request.EntryTicketRequest;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.response.EntryTicketResponse;

// Traduce entre el contrato HTTP (Request/Response) y los DTO de aplicacion.
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EntryTicketRestMapper {

    // Este método se queda "limpio"
    EntryTicketCreateDTO toCreateDTO(EntryTicketRequest request);

    // AQUÍ es donde realmente hacían falta las anotaciones
    @Mapping(target = "id", ignore = true)
    EntryTicketResponse toResponse(EntryTicketDTO entryTicket);

    Iterable<EntryTicketResponse> toResponseList(Iterable<EntryTicketDTO> entryTicket);
}