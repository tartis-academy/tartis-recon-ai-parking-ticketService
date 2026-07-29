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
                .uniqueId(ticketId)
                .stayId(stayId)
                .issuedAt(createDTO.issuedAt())
                .totalAmount(BigDecimal.ZERO)
                .build();
        TicketResponse expectedResponse = TicketResponse.builder()
                .uniqueId(ticketId)
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
                .uniqueId(ticketId)
                .stayId(stayId)
                .issuedAt(Instant.now())
                .totalAmount(BigDecimal.TEN)
                .build();
        TicketResponse expectedResponse = TicketResponse.builder()
                .uniqueId(ticketId)
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



}
