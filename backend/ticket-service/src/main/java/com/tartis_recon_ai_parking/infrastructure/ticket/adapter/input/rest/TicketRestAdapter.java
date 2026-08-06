package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

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
    // Solo ADMIN. La emision ordinaria del ticket de salida NO pasa por aqui:
    // la dispara ticket-service al consumir StayClosedEvent, asi que cerrar
    // este endpoint a OPERARIO no toca el flujo de check-out. Queda como
    // emision manual, que es una operacion de administracion.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        TicketDTO savedTicket = createUseCase.execute(mapper.toCreateDTO(request));
        return new ResponseEntity<>(mapper.toResponse(savedTicket), HttpStatus.CREATED);
    }

    /**
     * Lista los tickets, opcionalmente filtrados por stayId.
     * GET /v1/tickets
     */
    // OPERARIO entra en modo consulta: ve el listado pero no puede emitir
    // tickets (POST, arriba, exige ADMIN).
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERARIO')")
    public ResponseEntity<GetTicketUseCase.TicketPageDTO> listTickets(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        GetTicketUseCase.TicketPageDTO result = getTicketUseCase.getPage(search, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'OPERARIO')")
    public ResponseEntity<TicketResponse> getById(@PathVariable UUID id) {
        TicketDTO ticket = getTicketUseCase.getById(id);
        return ResponseEntity.ok(mapper.toResponse(ticket));
    }

}
