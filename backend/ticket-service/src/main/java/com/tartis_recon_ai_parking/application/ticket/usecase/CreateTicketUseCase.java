package com.tartis_recon_ai_parking.application.ticket.usecase;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.factory.TicketDTOFactory;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketEventPublisher;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketAlreadyExistsException;

public class CreateTicketUseCase {

    private final TicketPersistence ticketPersistence;
    private final TicketEventPublisher ticketEventPublisher;

    public CreateTicketUseCase(TicketPersistence ticketPersistence) {
        this(ticketPersistence, null);
    }

    @Autowired
    public CreateTicketUseCase(TicketPersistence ticketPersistence, @Autowired(required = false) TicketEventPublisher ticketEventPublisher) {
        this.ticketPersistence = ticketPersistence;
        this.ticketEventPublisher = ticketEventPublisher;
    }

    @Transactional
    public TicketDTO execute(TicketCreateDTO createDTO) throws InvalidTicketException {
        if (createDTO.stayId() != null && ticketPersistence.existsByStayId(createDTO.stayId())) {
            throw new TicketAlreadyExistsException(String.format("Ya existe un ticket para el stayId %s", createDTO.stayId()));
        }
        Ticket ticket = TicketDTOFactory.toDomain(createDTO);
        Ticket saved = ticketPersistence.save(ticket);

        if (ticketEventPublisher != null) {
            ticketEventPublisher.publish(TicketChangedEvent.of(saved, Instant.now()));
        }

        return TicketDTOFactory.toDTO(saved);
    }
}
