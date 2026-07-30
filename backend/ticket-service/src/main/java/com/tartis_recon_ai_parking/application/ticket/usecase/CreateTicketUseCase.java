package com.tartis_recon_ai_parking.application.ticket.usecase;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.factory.TicketDTOFactory;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;

public class CreateTicketUseCase {

    private final TicketPersistence ticketPersistence;

    public CreateTicketUseCase(TicketPersistence ticketPersistence) {
        this.ticketPersistence = ticketPersistence;
    }

    public TicketDTO execute(TicketCreateDTO createDTO) throws InvalidTicketException {
        if (createDTO.stayId() != null && ticketPersistence.existsByStayId(createDTO.stayId())) {
            throw new com.tartis_recon_ai_parking.domain.ticket.exception.TicketAlreadyExistsException("Ya existe un ticket para el stayId " + createDTO.stayId());
        }
        Ticket ticket = TicketDTOFactory.toDomain(createDTO);
        return TicketDTOFactory.toDTO(ticketPersistence.save(ticket));
    }
}

