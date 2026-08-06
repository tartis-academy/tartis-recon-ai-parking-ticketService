package com.tartis_recon_ai_parking.application.entryticket.usecase;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.factory.EntryTicketDTOFactory;
import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketEventPublisher;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;

public class CreateEntryTicketUseCase {
    
    private final EntryTicketPersistence entryTicketPersistence;
    private final TicketEventPublisher ticketEventPublisher;

    public CreateEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence) {
        this(entryTicketPersistence, null);
    }

    @Autowired
    public CreateEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence, @Autowired(required = false) TicketEventPublisher ticketEventPublisher) {
        this.entryTicketPersistence = entryTicketPersistence;
        this.ticketEventPublisher = ticketEventPublisher;
    }

    @Transactional
    public EntryTicketDTO execute(EntryTicketCreateDTO createDTO) throws InvalidEntryTicketException {
        EntryTicket entry = EntryTicketDTOFactory.toDomain(createDTO);
        EntryTicket saved = entryTicketPersistence.save(entry);

        if (ticketEventPublisher != null) {
            ticketEventPublisher.publish(TicketChangedEvent.of(saved, Instant.now()));
        }

        return EntryTicketDTOFactory.toDTO(saved);
    }
}
