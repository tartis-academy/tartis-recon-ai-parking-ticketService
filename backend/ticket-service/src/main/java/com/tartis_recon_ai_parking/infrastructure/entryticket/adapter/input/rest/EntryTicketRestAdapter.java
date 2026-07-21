package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest;

import java.util.UUID;

import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.request.EntryTicketRequest;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.response.EntryTicketResponse;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    private final EntryTicketRestMapper mapper;

    public EntryTicketRestAdapter(EntryTicketRestMapper mapper) {
        // Inicializar los casos de uso del ticket de entrada
        this.mapper = mapper;
    }

    //#####################################################################################################
    //# El código comentado en cada función es orientativo, no representa la implementación final.        #
    //# Este mensaje se ha escrito al acabar el esqueleto de los endpoints, antes de la implementación    #
    //# real de ninguno de ellos.                                                                         #
    //#####################################################################################################

    @GetMapping
    public ResponseEntity<Iterable<EntryTicketResponse>> getAllEntryTickets() {
        //Iterable<EntryTicketDTO> entryTickets; = ... ejecutar caso de uso getEntryTickets
        //return ResponseEntity.ok(mapper.toResponseList(entryTickets));
        return null; // MÉTODO POR IMPLEMENTAR
    }

    @GetMapping("/{id}/code")
    public ResponseEntity<EntryTicketResponse> getEntryTicketById(@PathVariable UUID id) throws EntryTicketNotFoundException {
        //EntryTicketDTO entryTickets; = ... ejecutar caso de uso getEntryTickets
        //return ResponseEntity.ok(mapper.toResponseList(entryTickets));
        return null; // MÉTODO POR IMPLEMENTAR
    }


    @PostMapping
    public ResponseEntity<EntryTicketResponse> createEntryTicket(@Valid @RequestBody EntryTicketRequest request) {
        //EntryTicketDTO savedEntryTicket = createEntryTicketUseCase.execute(mapper.toCreateDTO(request));
        //return new ResponseEntity<>(mapper.toResponse(savedEntryTicket), HttpStatus.CREATED);
        return null; // MÉTODO POR IMPLEMENTAR
    }


    @PutMapping("/{id}")
    public ResponseEntity<EntryTicketResponse> updateEntryTicket(@PathVariable UUID id,
                @Valid @RequestBody EntryTicketRequest request) throws EntryTicketNotFoundException {
                                                            
        //EntryTicketDTO updatedEntryTicket = updateEntryTicketUseCase.execute(id, mapper.toCreateDTO(request));
        //return ResponseEntity.ok(mapper.toResponse(updatedEntryTicket));
        return null; // MÉTODO POR IMPLEMENTAR
    }
}

    
    
