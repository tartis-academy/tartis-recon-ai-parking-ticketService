package com.tartis_recon_ai_parking.application.entryticket.factory;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;

public final class EntryTicketDTOFactory {
    
    private EntryTicketDTOFactory() {}
    
    public static EntryTicketDTO toDTO(EntryTicket entry){
        return new EntryTicketDTO(entry.getUniqueId(), entry.getStayId(), entry.getCode(), entry.getIssuedAt(), entry.getSt)
    }
}
