package com.tartis_recon_ai_parking.application.ticket.usecase;

import java.time.Instant;

import org.springframework.transaction.annotation.Transactional;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.factory.TicketDTOFactory;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketAlreadyExistsException;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketEventPublisher;



public class CreateTicketUseCase {

    private final TicketPersistence ticketPersistence;
    private final TicketEventPublisher ticketEventPublisher;

    public CreateTicketUseCase(TicketPersistence ticketPersistence, TicketEventPublisher ticketEventPublisher) {
        this.ticketPersistence = ticketPersistence;
        this.ticketEventPublisher = ticketEventPublisher;
    }

    // @Transactional es obligatorio: el relay escucha en AFTER_COMMIT y sin
    // transaccion activa el evento se descarta sin error ni log.
    @Transactional
    public TicketDTO execute(TicketCreateDTO createDTO) throws InvalidTicketException {
        if (createDTO.stayId() != null && ticketPersistence.existsByStayId(createDTO.stayId())) {
            throw new TicketAlreadyExistsException(String.format("Ya existe un ticket para el stayId %s", createDTO.stayId()));
        }
        Ticket ticket = TicketDTOFactory.toDomain(createDTO);
        Ticket saved = ticketPersistence.save(ticket);
        ticketEventPublisher.publish(TicketChangedEvent.of(saved, Instant.now()));
        return TicketDTOFactory.toDTO(saved);
    }
}

