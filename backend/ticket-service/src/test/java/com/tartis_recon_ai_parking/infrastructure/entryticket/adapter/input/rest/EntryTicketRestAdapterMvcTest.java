package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest;

import java.time.Instant;
import java.util.UUID;

import com.tartis_recon_ai_parking.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.usecase.CreateEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.usecase.GetEntryTicketUseCase;
import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EntryTicketRestAdapter.class)
@Import({EntryTicketRestMapperImpl.class, SecurityConfig.class})
class EntryTicketRestAdapterMvcTest {

    private static final String URL = "/v1/entry-tickets/{id}/code";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetEntryTicketUseCase getUseCase;

    // Necesario aunque no se use en estos tests: el constructor del adapter lo exige.
    @MockitoBean
    private CreateEntryTicketUseCase createUseCase;

    @Test
    @DisplayName("GET /{id}/code devuelve 200 y el ticket completo cuando el id existe")
    void shouldReturn200WithTicket() throws Exception {
        UUID id = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        String code = "ABC23456DE";

        when(getUseCase.execute(id))
                .thenReturn(new EntryTicketDTO(id, stayId, Instant.now(), code));

        mockMvc.perform(get(URL, id)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.stayId").value(stayId.toString()))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.issuedAt").exists());
    }

    @Test
    @DisplayName("GET /{id}/code devuelve 404 cuando el id no existe")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(getUseCase.execute(id))
                .thenThrow(new EntryTicketNotFoundException("EntryTicket not found: " + id));

        mockMvc.perform(get(URL, id)
                        .with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /{id}/code devuelve 400 cuando el id no es un UUID valido")
    void shouldReturn400WhenIdIsNotUuid() throws Exception {
        mockMvc.perform(get(URL, "no-soy-un-uuid")
                        .with(jwt()))
                .andExpect(status().isBadRequest());
    }
}