package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.tartis_recon_ai_parking.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.usecase.CreateEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.usecase.GetEntryTicketUseCase;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EntryTicketRestAdapter.class)
@Import({EntryTicketRestMapperImpl.class, SecurityConfig.class})
class EntryTicketRestAdapterPostMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateEntryTicketUseCase createUseCase;

    @MockitoBean
    private GetEntryTicketUseCase getUseCase;

    @Test
    @DisplayName("POST /v1/entry-tickets debería devolver 201 con el ticket creado")
    void createEntryTicket_shouldReturn201() throws Exception {
        UUID stayId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        Instant now = Instant.now();
        String code = "NEWCODE123";

        when(createUseCase.execute(any()))
                .thenReturn(new EntryTicketDTO(ticketId, stayId, now, code));

        mockMvc.perform(post("/v1/entry-tickets")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + stayId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ticketId.toString()))
                .andExpect(jsonPath("$.stayId").value(stayId.toString()))
                .andExpect(jsonPath("$.code").value(code));
    }

    @Test
    @DisplayName("POST /v1/entry-tickets debería devolver 400 cuando stayId es inválido")
    void createEntryTicket_shouldReturn400WhenInvalid() throws Exception {
        when(createUseCase.execute(any()))
                .thenThrow(new InvalidEntryTicketException("stayId is null"));

        mockMvc.perform(post("/v1/entry-tickets")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /v1/entry-tickets debería devolver 200 con lista vacía")
    void getAllEntryTickets_shouldReturnEmptyList() throws Exception {
        when(getUseCase.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/v1/entry-tickets")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /v1/entry-tickets debería devolver 200 con elementos")
    void getAllEntryTickets_shouldReturnItems() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(getUseCase.getAll()).thenReturn(List.of(
                new EntryTicketDTO(id1, UUID.randomUUID(), Instant.now(), "CODE1"),
                new EntryTicketDTO(id2, UUID.randomUUID(), Instant.now(), "CODE2")
        ));

        mockMvc.perform(get("/v1/entry-tickets")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
}

