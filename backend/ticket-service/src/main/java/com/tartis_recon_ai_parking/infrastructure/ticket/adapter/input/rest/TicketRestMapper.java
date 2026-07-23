package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response.TicketResponse;

@Component
public class TicketRestMapper {

    public TicketResponse toResponse(TicketDTO ticket) {
        if (ticket == null) {
            return null;
           
        }

        return new TicketResponse(ticket.getId(), ticket.getStayId(),ticket.getissuedAt(),ticket.getTotalAmount()
           
        );
    }

}
