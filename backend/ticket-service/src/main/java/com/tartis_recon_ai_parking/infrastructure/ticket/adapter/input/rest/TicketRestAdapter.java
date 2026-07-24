package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.request.TicketRequest;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response.TicketResponse;

@RestController
@RequestMapping("/v1/tickets")
public class TicketRestAdapter {

    private final CreateTicketUseCase createUseCase;
    private final TicketRestMapper mapper;

    public TicketRestAdapter(TicketRestMapper mapper, CreateTicketUseCase createUseCase) {
        this.createUseCase = createUseCase;
        this.mapper = mapper;
    }

    /**
     * Genera el ticket de salida/pago para una estancia finalizada.
     * POST /v1/tickets
     */
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        TicketDTO savedTicket = createUseCase.execute(mapper.toCreateDTO(request));
        return new ResponseEntity<>(mapper.toResponse(savedTicket), HttpStatus.CREATED);
    }

    /**
     * Lista los tickets, opcionalmente filtrados por stayId.
     * GET /v1/tickets
     */
    @GetMapping
    public ResponseEntity<Void> listTickets(@RequestParam(required = false) UUID stayId) {
        // TODO: implementar listado de tickets
        return null;
    }

    /**
     * Recupera un ticket por su ID.
     * GET /v1/tickets/{ticketId}
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<Void> getTicketById(@PathVariable UUID ticketId) {
        // TODO: implementar consulta de ticket por id
        return null;
    }

    /**
     * Marca un ticket como PERDIDO (IN-22): aplica tarifa de penalizacion
     * en vez de la tarifa normal.
     * PATCH /v1/tickets/{ticketId}/lost
     */
    @PatchMapping("/{ticketId}/lost")
    public ResponseEntity<Void> markTicketLost(@PathVariable UUID ticketId) {
        // TODO: implementar marcado de ticket como perdido
        return null;
    }
}

