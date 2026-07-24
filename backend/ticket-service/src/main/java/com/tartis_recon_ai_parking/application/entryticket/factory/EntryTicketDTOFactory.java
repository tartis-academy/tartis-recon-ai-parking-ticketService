package com.tartis_recon_ai_parking.application.entryticket.factory;

import java.util.List;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;

public final class EntryTicketDTOFactory {
    
    private EntryTicketDTOFactory() {}
    
    public static EntryTicketDTO toDTO(EntryTicket entry) {
        return new EntryTicketDTO(entry.getUniqueId(), entry.getStayId(), entry.getIssuedAt(), entry.getCode());
    }
    public static EntryTicket toDomain(EntryTicketCreateDTO dto){
        return EntryTicket.create(dto.stayId());       
    }

    public static List<EntryTicketDTO> toDTOList(List<EntryTicket> entryTickets) {
        return entryTickets.stream().map(EntryTicketDTOFactory::toDTO).toList();
    }
    
}
