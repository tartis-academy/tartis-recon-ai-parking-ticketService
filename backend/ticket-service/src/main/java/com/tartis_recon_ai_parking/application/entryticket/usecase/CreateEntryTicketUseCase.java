package com.tartis_recon_ai_parking.application.entryticket.usecase;

import java.time.Instant;
import org.springframework.transaction.annotation.Transactional;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.factory.EntryTicketDTOFactory;
import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketEventPublisher;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketAlreadyExistsException;

public class CreateEntryTicketUseCase {
    
    private final EntryTicketPersistence entryTicketPersistence;
    private final TicketEventPublisher ticketEventPublisher;

    public CreateEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence, TicketEventPublisher ticketEventPublisher) {
        this.entryTicketPersistence = entryTicketPersistence;
        this.ticketEventPublisher = ticketEventPublisher;
    }

    // @Transactional es obligatorio: el relay escucha en AFTER_COMMIT y sin
    // transacción activa el evento se descarta sin error ni log.
    @Transactional
    public EntryTicketDTO execute(EntryTicketCreateDTO createDTO) throws InvalidEntryTicketException {
        // 1. Control de Idempotencia
        if (createDTO.stayId() != null && entryTicketPersistence.findByStayId(createDTO.stayId()).isPresent()) {
            throw new TicketAlreadyExistsException("EntryTicket ya existe para stayId: " + createDTO.stayId());
        }

        // 2. Creación y Persistencia del Dominio
        EntryTicket entry = EntryTicketDTOFactory.toDomain(createDTO);
        EntryTicket saved = entryTicketPersistence.save(entry);

        // 3. Publicación del Evento (tras COMMIT exitoso)
        ticketEventPublisher.publish(TicketChangedEvent.of(saved, Instant.now()));

        // 4. Mapeo a DTO de salida
        return EntryTicketDTOFactory.toDTO(saved);
    }
}
