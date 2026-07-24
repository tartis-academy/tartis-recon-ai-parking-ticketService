package com.tartis_recon_ai_parking.application.entryticket.usecase;

import java.util.List;
import java.util.stream.Collectors;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.factory.EntryTicketDTOFactory;
import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;

public class GetEntryTicketUseCase {
    private final EntryTicketPersistence entryTicketPersistence;

    public GetEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence){
        this.entryTicketPersistence = entryTicketPersistence;
    }

    public  List<EntryTicketDTO> getAll() {
        
       return EntryTicketDTOFactory.toDTOList( entryTicketPersistence.findAll());

    }
}
