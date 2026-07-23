package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.usecase.GetTicketUseCase;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response.TicketResponse;


@RestController
@RequestMapping("/v1/ticket")
public class TicketRestAdapter {

    private final GetTicketUseCase getTicketUseCase;
    private final TicketRestMapper ticketRestMapper;
    public TicketRestAdapter (TicketRestMapper ticketRestMapper, GetTicketUseCase getTicketUseCase){
        this.getTicketUseCase=getTicketUseCase;
        this.ticketRestMapper=ticketRestMapper;
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable UUID id) {
        TicketDTO ticket = getTicketUseCase.getById(id);
         if (ticket == null) {
        return ResponseEntity.notFound().build();
    }
        return ResponseEntity.ok(ticketRestMapper.toResponse(ticket));
    }

}
