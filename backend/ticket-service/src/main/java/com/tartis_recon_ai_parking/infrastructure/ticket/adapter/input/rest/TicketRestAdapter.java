package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.application.ticket.usecase.GetTicketUseCase;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.request.TicketRequest;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response.TicketResponse;

@RestController
@RequestMapping("/v1/tickets")
public class TicketRestAdapter {

    private final CreateTicketUseCase createUseCase;
    private final TicketRestMapper mapper;
    private final GetTicketUseCase getTicketUseCase;

    public TicketRestAdapter(TicketRestMapper mapper, CreateTicketUseCase createUseCase, GetTicketUseCase getTicketUseCase) {
        this.createUseCase = createUseCase;
        this.mapper = mapper;
        this.getTicketUseCase = getTicketUseCase;
    }

    /**
     * Genera el ticket de salida/pago para una estancia finalizada.
     * POST /v1/tickets
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERARIO')")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        TicketDTO savedTicket = createUseCase.execute(mapper.toCreateDTO(request));
        return new ResponseEntity<>(mapper.toResponse(savedTicket), HttpStatus.CREATED);
    }

    /**
     * Lista los tickets, opcionalmente filtrados por stayId.
     * GET /v1/tickets
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TicketResponse>> listTickets(
            @RequestParam(required = false) UUID stayId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TicketDTO> tickets = getTicketUseCase.getAllPaginated(page, size);
        return ResponseEntity.ok(tickets.map(mapper::toResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'OPERARIO')")
    public ResponseEntity<TicketResponse> getById(@PathVariable UUID id) {
        TicketDTO ticket = getTicketUseCase.getById(id);
        return ResponseEntity.ok(mapper.toResponse(ticket));
    }

}
