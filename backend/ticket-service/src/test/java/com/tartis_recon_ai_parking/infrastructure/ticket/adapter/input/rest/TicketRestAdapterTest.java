package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.application.ticket.usecase.GetTicketUseCase;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.request.TicketRequest;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response.TicketResponse;

@ExtendWith(MockitoExtension.class)
class TicketRestAdapterTest {

    @Mock
    private CreateTicketUseCase createUseCase;

    @Mock
    private TicketRestMapper mapper;

    @Mock
    private GetTicketUseCase getTicketUseCase;

    @InjectMocks
    private TicketRestAdapter adapter;

    private UUID stayId;
    private UUID ticketId;

    @BeforeEach
    void setUp() {
        stayId = UUID.randomUUID();
        ticketId = UUID.randomUUID();
    }

    @Test
    void createTicket_ShouldReturnCreatedWithMappedResponse() {
        TicketRequest request = TicketRequest.builder().stayId(stayId).build();
        TicketCreateDTO createDTO = new TicketCreateDTO(stayId, Instant.now(), BigDecimal.ZERO);
        TicketDTO savedDto = TicketDTO.builder()
                .id(ticketId)
                .stayId(stayId)
                .issuedAt(createDTO.issuedAt())
                .totalAmount(BigDecimal.ZERO)
                .build();
        TicketResponse expectedResponse = TicketResponse.builder()
                .id(ticketId)
                .stayId(stayId)
                .issuedAt(createDTO.issuedAt())
                .totalAmount(BigDecimal.ZERO)
                .build();

        when(mapper.toCreateDTO(request)).thenReturn(createDTO);
        when(createUseCase.execute(createDTO)).thenReturn(savedDto);
        when(mapper.toResponse(savedDto)).thenReturn(expectedResponse);

        ResponseEntity<TicketResponse> result = adapter.createTicket(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(createUseCase).execute(createDTO);
        verify(mapper).toResponse(savedDto);
    }

    @Test
    void getById_WhenTicketExists_ShouldReturnOkWithMappedResponse() {
        TicketDTO dto = TicketDTO.builder()
                .id(ticketId)
                .stayId(stayId)
                .issuedAt(Instant.now())
                .totalAmount(BigDecimal.TEN)
                .build();
        TicketResponse expectedResponse = TicketResponse.builder()
                .id(ticketId)
                .stayId(stayId)
                .issuedAt(dto.getIssuedAt())
                .totalAmount(BigDecimal.TEN)
                .build();

        when(getTicketUseCase.getById(ticketId)).thenReturn(dto);
        when(mapper.toResponse(dto)).thenReturn(expectedResponse);

        ResponseEntity<TicketResponse> result = adapter.getById(ticketId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
    }

    // NOTA: en el flujo real, GetTicketUseCase.getById lanza TicketNotFoundException
    // en lugar de devolver null, por lo que esta rama del adaptador (if (ticket == null))
    // no se alcanza en producción. Se testea igualmente porque forma parte del código actual.
    @Test
    void getById_WhenUseCaseReturnsNull_ShouldReturnNotFound() {
        when(getTicketUseCase.getById(ticketId)).thenReturn(null);

        ResponseEntity<TicketResponse> result = adapter.getById(ticketId);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isNull();
        verify(mapper, never()).toResponse(any());
    }

    // NOTA: endpoint aun no implementado (TODO en el codigo fuente).
    // Este test documenta el comportamiento actual (siempre devuelve null)
    // y debera actualizarse cuando se implemente el listado real.
    @Test
    void listTickets_WhenNotImplemented_ShouldReturnNull() {
        ResponseEntity<Void> result = adapter.listTickets(stayId);

        assertThat(result).isNull();
    }

    // NOTA: endpoint aun no implementado (TODO en el codigo fuente).
    // Este test documenta el comportamiento actual y debera actualizarse
    // cuando se implemente la logica de marcado de ticket como perdido.
    @Test
    void markTicketLost_WhenNotImplemented_ShouldReturnNull() {
        ResponseEntity<Void> result = adapter.markTicketLost(ticketId);

        assertThat(result).isNull();
    }
}
