package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest;

import java.util.UUID;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.usecase.CreateEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.usecase.GetEntryTicketUseCase;
import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.request.EntryTicketRequest;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.response.EntryTicketResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/entry-tickets")
public class EntryTicketRestAdapter {

    //ATRIBUTOS CORRESPONDIENTES A LOS CASOS DE USO DEL TICKET DE ENTRADA
    private final CreateEntryTicketUseCase createUseCase;
    private final GetEntryTicketUseCase getUseCase;
    private final EntryTicketRestMapper mapper;

    public EntryTicketRestAdapter(EntryTicketRestMapper mapper, CreateEntryTicketUseCase createEntryTicketUseCase,
            GetEntryTicketUseCase getEntryTicketUseCase) {
        // Inicializar los casos de uso del ticket de entrada
        this.createUseCase = createEntryTicketUseCase;
        this.getUseCase = getEntryTicketUseCase;
        this.mapper = mapper;
    }

    //#####################################################################################################
    //# El código comentado en cada función es orientativo, no representa la implementación final.        #
    //# Este mensaje se ha escrito al acabar el esqueleto de los endpoints, antes de la implementación    #
    //# real de ninguno de ellos.                                                                         #
    //#####################################################################################################

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Iterable<EntryTicketResponse>> getAllEntryTickets() {
        Iterable<EntryTicketDTO> entryTikets = getUseCase.getAll();
        return ResponseEntity.ok(mapper.toResponseList(entryTikets));
    }

    @GetMapping("/{id}/code")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'OPERARIO')")
    public ResponseEntity<EntryTicketResponse> getEntryTicketById(@PathVariable UUID id) throws EntryTicketNotFoundException {
        EntryTicketDTO entryTicket = getUseCase.execute(id);
        return ResponseEntity.ok(mapper.toResponse(entryTicket));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERARIO')")
    public ResponseEntity<EntryTicketResponse> createEntryTicket(@Valid @RequestBody EntryTicketRequest request) {
        EntryTicketDTO savedEntryTicket = createUseCase.execute(mapper.toCreateDTO(request));
        return new ResponseEntity<>(mapper.toResponse(savedEntryTicket), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntryTicketResponse> updateEntryTicket(@PathVariable UUID id,
                @Valid @RequestBody EntryTicketRequest request) throws EntryTicketNotFoundException {
                                                            
        //EntryTicketDTO updatedEntryTicket = updateEntryTicketUseCase.execute(id, mapper.toCreateDTO(request));
        //return ResponseEntity.ok(mapper.toResponse(updatedEntryTicket));
        return null; // MÉTODO POR IMPLEMENTAR
    }
}
