package com.tartis_recon_ai_parking.application.ticket.usecase;

import java.util.List;
import java.util.UUID;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.factory.TicketDTOFactory;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketNotFoundException;

public class GetTicketUseCase {
    private final TicketPersistence persistence;

    public GetTicketUseCase(TicketPersistence persistence){
        this.persistence=persistence;
    }

    public TicketDTO getById (UUID id){
        return persistence.findById(id).map(TicketDTOFactory::toDTO).orElseThrow(() -> new TicketNotFoundException("No existe un ticket con id " + id));
    }

    public List<TicketDTO> getAll() {
        return TicketDTOFactory.toDTOList(persistence.findAll());
    }

    public TicketPageDTO getPage(String search, int page, int size) {
        if (search != null && search.trim().isEmpty()) {
            search = null;
        }
        TicketPersistence.TicketPage result = persistence.findPage(search, page, size);
        List<TicketDTO> content = TicketDTOFactory.toDTOList(result.content());
        return new TicketPageDTO(content, result.page(), result.size(), result.totalElements(), result.totalPages());
    }

    public record TicketPageDTO(List<TicketDTO> content, int page, int size, long totalElements, int totalPages) {}
}
