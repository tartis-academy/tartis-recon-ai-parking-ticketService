package com.tartis_recon_ai_parking.application.entryticket.usecase;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.factory.EntryTicketDTOFactory;
import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;

public class CreateEntryTicketUseCase {
    
    private final EntryTicketPersistence entryTicketPersistence;

    public CreateEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence){
        this.entryTicketPersistence = entryTicketPersistence;
    }

    public EntryTicketDTO execute(EntryTicketCreateDTO createDTO) throws InvalidEntryTicketException{
        
        EntryTicket entry = EntryTicketDTOFactory.toDomain(createDTO);
        
        return EntryTicketDTOFactory.toDTO(entryTicketPersistence.save(entry));

    }

}
