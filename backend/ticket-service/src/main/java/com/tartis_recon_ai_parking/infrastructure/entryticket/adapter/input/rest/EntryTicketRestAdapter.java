package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest;

import java.util.UUID;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.request.EntryTicketRequest;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.response.EntryTicketResponse;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/v1/vehicles")

public class EntryTicketRestAdapter {

    //ATRIBUTOS CORRESPONDIENTES A LOS CASOS DE USO DEL TICKET DE ENTRADA
    EntryTicketRestMapper mapper;

    public EntryTicketRestAdapter(EntryTicketRestMapper mapper) {
        // Inicializar los casos de uso del ticket de entrada
        this.mapper = mapper;
    }

    // Pendiente: @RestController @RequestMapping("/v1/entry-tickets")
    // POST /            -> emitir (check-in)
    // PATCH /{id}/use   -> consumir (check-out, IN-21)
    // PATCH /{id}/lost  -> marcar perdido (IN-22)
    // GET /{id}/barcode.png -> imagen generada al vuelo (ZXing), no persistida

    //Nota de José Ángel:
    //#####################################################################################################
    //# El código comentado en cada función es orientativo, no representa la implementación final.        #
    //# Este mensaje se ha escrito al acabar el esqueleto de los endpoints, antes de la implementación    #
    //# real de ninguno de ellos.                                                                         #
    //#####################################################################################################

    @GetMapping
    public ResponseEntity<Iterable<EntryTicketResponse>> getAllEntryTickets() {
        //Iterable<EntryTicketDTO> entryTickets; = ... ejecutar caso de uso getEntryTickets
        //return ResponseEntity.ok(mapper.toResponseList(entryTickets));
        return null; // LINEA PROVISIONAL
    }

    @GetMapping("/{id}/code")
    public ResponseEntity<EntryTicketResponse> getEntryTicketById(@PathVariable UUID id) throws EntryTicketNotFoundException {
        //EntryTicketDTO entryTickets; = ... ejecutar caso de uso getEntryTickets
        //return ResponseEntity.ok(mapper.toResponseList(entryTickets));
        return null; // LINEA PROVISIONAL
    }


    @PostMapping
    public ResponseEntity<EntryTicketResponse> createEntryTicket(@Valid @RequestBody EntryTicketRequest request) {
        //EntryTicketDTO savedEntryTicket = createEntryTicketUseCase.execute(mapper.toCreateDTO(request));
        //return new ResponseEntity<>(mapper.toResponse(savedEntryTicket), HttpStatus.CREATED);
        return null; // LINEA PROVISIONAL
    }


    @PutMapping("/{id}")
    public ResponseEntity<EntryTicketResponse> updateEntryTicket(@PathVariable UUID id,
                @Valid @RequestBody EntryTicketRequest request) throws EntryTicketNotFoundException {
                                                            
        //EntryTicketDTO updatedEntryTicket = updateEntryTicketUseCase.execute(id, mapper.toCreateDTO(request));
        //return ResponseEntity.ok(mapper.toResponse(updatedEntryTicket));
        return null; // LINEA PROVISIONAL
    }
}

    
    
