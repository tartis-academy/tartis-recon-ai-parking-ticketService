package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.application.ticket.usecase.GetTicketUseCase;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketRestAdapter.class)
@Import(TicketRestMapperImpl.class)
class TicketRestAdapterMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateTicketUseCase createUseCase;

    @MockitoBean
    private GetTicketUseCase getTicketUseCase;

    @Test
    @DisplayName("POST /v1/tickets debería devolver 201 con el ticket creado")
    void createTicket_shouldReturn201() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();

        TicketDTO savedDto = TicketDTO.builder()
                .id(ticketId)
                .stayId(stayId)
                .issuedAt(issuedAt)
                .totalAmount(BigDecimal.ZERO)
                .build();

        when(createUseCase.execute(any())).thenReturn(savedDto);

        mockMvc.perform(post("/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + stayId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ticketId.toString()))
                .andExpect(jsonPath("$.stayId").value(stayId.toString()));
    }

    @Test
    @DisplayName("GET /v1/tickets/{id} debería devolver 200 cuando existe")
    void getById_shouldReturn200WhenExists() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();

        TicketDTO dto = TicketDTO.builder()
                .id(ticketId)
                .stayId(stayId)
                .issuedAt(issuedAt)
                .totalAmount(BigDecimal.valueOf(25.50))
                .build();

        when(getTicketUseCase.getById(ticketId)).thenReturn(dto);

        mockMvc.perform(get("/v1/tickets/{id}", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId.toString()))
                .andExpect(jsonPath("$.stayId").value(stayId.toString()))
                .andExpect(jsonPath("$.totalAmount").value(25.50));
    }

    @Test
    @DisplayName("GET /v1/tickets/{id} debería propagar TicketNotFoundException cuando no existe")
    void getById_shouldThrowWhenNotFound() {
        UUID ticketId = UUID.randomUUID();
        when(getTicketUseCase.getById(ticketId))
                .thenThrow(new TicketNotFoundException("No existe un ticket con id " + ticketId));

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        mockMvc.perform(get("/v1/tickets/{id}", ticketId)))
                .hasCauseInstanceOf(TicketNotFoundException.class);
    }

    @Test
    @DisplayName("POST /v1/tickets debería devolver 400 con body vacío")
    void createTicket_shouldReturn400WithEmptyBody() throws Exception {
        mockMvc.perform(post("/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /v1/tickets/{id} debería devolver 400 con id inválido")
    void getById_shouldReturn400WithInvalidUuid() throws Exception {
        mockMvc.perform(get("/v1/tickets/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }
}
