package com.tartis_recon_ai_parking.application.ticket.factory;

import java.util.List;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.domain.ticket.Ticket;
public final class TicketDTOFactory {

    private TicketDTOFactory(){}

public static TicketDTO toDTO(Ticket ticket) {
        return new TicketDTO(
                ticket.getUniqueId(),
                ticket.getStayId(),
                ticket.getIssuedAt(),
                ticket.getTotalAmount());
    }

    public static List<TicketDTO> toDTOList(List<Ticket> ticket) {
        return ticket.stream().map(TicketDTOFactory::toDTO).toList();
    }

    //tendria q usar el create 
    public static Ticket toDomain(TicketCreateDTO dto) {
        return Ticket.create(
                dto.stayId(),
                dto.issuedAt(),
                dto.totalAmount());
    }
}
