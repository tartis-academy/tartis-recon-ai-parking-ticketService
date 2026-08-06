package com.tartis_recon_ai_parking.application.entryticket.usecase;

import java.time.Instant;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.factory.EntryTicketDTOFactory;
import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketEventPublisher;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;

public class UpdateEntryTicketUseCase {

    private final EntryTicketPersistence entryTicketPersistence;
    private final TicketEventPublisher ticketEventPublisher;

    public UpdateEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence, TicketEventPublisher ticketEventPublisher) {
        this.entryTicketPersistence = entryTicketPersistence;
        this.ticketEventPublisher = ticketEventPublisher;
    }

    // @Transactional es obligatorio: el relay escucha en AFTER_COMMIT y sin
    // transaccion activa el evento se descarta sin error ni log.
    @Transactional
    public EntryTicketDTO execute(UUID id, EntryTicketCreateDTO dto) {
        EntryTicket existing = entryTicketPersistence.findByIdForUpdate(id)
                .orElseThrow(() -> new EntryTicketNotFoundException("EntryTicket not found: " + id));

        EntryTicket updated = EntryTicket.recreate(
                existing.getUniqueId(),
                dto.stayId(),
                existing.getIssuedAt(),
                existing.getCode()
        );

        EntryTicket saved = entryTicketPersistence.save(updated);
        ticketEventPublisher.publish(TicketChangedEvent.of(saved, "UPDATED", Instant.now()));
        return EntryTicketDTOFactory.toDTO(saved);
    }
}
