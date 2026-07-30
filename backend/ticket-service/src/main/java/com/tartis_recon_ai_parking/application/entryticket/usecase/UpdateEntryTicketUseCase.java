package com.tartis_recon_ai_parking.application.entryticket.usecase;

import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.factory.EntryTicketDTOFactory;
import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;

public class UpdateEntryTicketUseCase {

    private final EntryTicketPersistence entryTicketPersistence;

    public UpdateEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence) {
        this.entryTicketPersistence = entryTicketPersistence;
    }

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
        return EntryTicketDTOFactory.toDTO(saved);
    }
}
