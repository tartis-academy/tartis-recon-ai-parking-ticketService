package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.usecase.CreateEntryTicketUseCase;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.request.EntryTicketRequest;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.response.EntryTicketResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntryTicketRestAdapterTest {

    @Mock
    private CreateEntryTicketUseCase createUseCase;

    @Mock
    private EntryTicketRestMapper mapper;

    @InjectMocks
    private EntryTicketRestAdapter adapter;

    @Test
    @DisplayName("Debe crear un ticket de entrada correctamente llamando al caso de uso y devolviendo 201 Created")
    void shouldCreateEntryTicketSuccessfully() {
        // QUE HACE: 
        // Generar datos ficticios, preparar mocks y llamar al adapter.
        UUID stayId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        Instant now = Instant.now();
        String code = "TICKET-123";

        EntryTicketRequest request = new EntryTicketRequest(stayId);
        EntryTicketCreateDTO createDTO = new EntryTicketCreateDTO(stayId);
        EntryTicketDTO savedDTO = new EntryTicketDTO(ticketId, stayId, code, now);
        EntryTicketResponse response = new EntryTicketResponse(ticketId, stayId, code, now);

        when(mapper.toCreateDTO(request)).thenReturn(createDTO);
        when(createUseCase.execute(createDTO)).thenReturn(savedDTO);
        when(mapper.toResponse(savedDTO)).thenReturn(response);

        // ACT
        ResponseEntity<EntryTicketResponse> result = adapter.createEntryTicket(request);

        // QUE DEBERIA HACER: 
        // Retornar código HTTP 201 CREATED y la respuesta mapeada.
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);

        // Verificar interacciones
        verify(mapper, times(1)).toCreateDTO(request);
        verify(createUseCase, times(1)).execute(createDTO);
        verify(mapper, times(1)).toResponse(savedDTO);
    }

    @Test
    @DisplayName("Debe propagar InvalidEntryTicketException si se introduce un dato nulo o inválido")
    void shouldThrowExceptionWhenDataIsInvalid() throws InvalidEntryTicketException {
        // QUE HACE: 
        // Simular una petición con datos incompletos (ej. stayId nulo)
        EntryTicketRequest request = new EntryTicketRequest(null);
        EntryTicketCreateDTO createDTO = new EntryTicketCreateDTO(null);

        when(mapper.toCreateDTO(request)).thenReturn(createDTO);
        when(createUseCase.execute(createDTO)).thenThrow(new InvalidEntryTicketException("stayId is null"));

        // ACT & QUE DEBERIA HACER: 
        // Debe lanzar la excepción, que posteriormente un @ControllerAdvice capturaría
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> adapter.createEntryTicket(request))
                .isInstanceOf(InvalidEntryTicketException.class)
                .hasMessageContaining("stayId is null");

        verify(mapper, times(1)).toCreateDTO(request);
        verify(createUseCase, times(1)).execute(createDTO);
        verify(mapper, never()).toResponse(any());
    }
}
