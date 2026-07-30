package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.usecase.CreateEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.usecase.GetEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.usecase.UpdateEntryTicketUseCase;
import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;
import com.tartis_recon_ai_parking.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EntryTicketRestAdapter.class)
@Import({EntryTicketRestMapperImpl.class, SecurityConfig.class})
class EntryTicketRestAdapterMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetEntryTicketUseCase getUseCase;

    @MockitoBean
    private CreateEntryTicketUseCase createUseCase;

    @MockitoBean
    private UpdateEntryTicketUseCase updateUseCase;



    @Test
    @DisplayName("GET /v1/entry-tickets deberia devolver 200 con lista vacia")
    void getAllEntryTickets_shouldReturn200WithEmptyList() throws Exception {
        when(getUseCase.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/v1/entry-tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(getUseCase, times(1)).getAll();
    }

    @Test
    @DisplayName("GET /v1/entry-tickets deberia devolver 200 con elementos")
    void getAllEntryTickets_shouldReturn200WithItems() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(getUseCase.getAll()).thenReturn(List.of(
                new EntryTicketDTO(id1, UUID.randomUUID(), Instant.now(), "CODE1"),
                new EntryTicketDTO(id2, UUID.randomUUID(), Instant.now(), "CODE2")
        ));

        mockMvc.perform(get("/v1/entry-tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /v1/entry-tickets/{id}/code deberia devolver 200 con el ticket completo cuando el id existe")
    void getEntryTicketByCode_shouldReturn200WithTicket() throws Exception {
        UUID id = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        String code = "ABC23456DE";

        when(getUseCase.execute(id))
                .thenReturn(new EntryTicketDTO(id, stayId, Instant.now(), code));

        mockMvc.perform(get("/v1/entry-tickets/{id}/code", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.stayId").value(stayId.toString()))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.issuedAt").exists());
    }

    @Test
    @DisplayName("GET /v1/entry-tickets/{id}/code deberia devolver 404 cuando el id no existe")
    void getEntryTicketByCode_shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(getUseCase.execute(id))
                .thenThrow(new EntryTicketNotFoundException("EntryTicket not found: " + id));

        mockMvc.perform(get("/v1/entry-tickets/{id}/code", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /v1/entry-tickets/{id}/code deberia devolver 400 cuando el id no es un UUID valido")
    void getEntryTicketByCode_shouldReturn400WhenIdIsNotUuid() throws Exception {
        mockMvc.perform(get("/v1/entry-tickets/{id}/code", "no-soy-un-uuid")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /v1/entry-tickets deberia devolver 201 con el ticket creado")
    void createEntryTicket_shouldReturn201() throws Exception {
        UUID stayId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        String code = "NEWCODE123";

        when(createUseCase.execute(any()))
                .thenReturn(new EntryTicketDTO(ticketId, stayId, Instant.now(), code));

        mockMvc.perform(post("/v1/entry-tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + stayId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ticketId.toString()))
                .andExpect(jsonPath("$.stayId").value(stayId.toString()))
                .andExpect(jsonPath("$.code").value(code));
    }

    @Test
    @DisplayName("POST /v1/entry-tickets deberia devolver 400 cuando stayId es invalido")
    void createEntryTicket_shouldReturn400WhenInvalid() throws Exception {
        when(createUseCase.execute(any()))
                .thenThrow(new InvalidEntryTicketException("stayId is null"));

        mockMvc.perform(post("/v1/entry-tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /v1/entry-tickets/{id} deberia devolver 200 para ADMIN")
    void updateEntryTicket_shouldReturn200ForAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        String code = "UPDATEDCODE";
        Instant now = Instant.now();

        when(updateUseCase.execute(eq(id), any()))
                .thenReturn(new EntryTicketDTO(id, stayId, now, code));

        mockMvc.perform(put("/v1/entry-tickets/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + stayId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.stayId").value(stayId.toString()))
                .andExpect(jsonPath("$.code").value(code));
    }


    @Test
    @DisplayName("Debe rechazar con 401 una peticion sin token")
    void shouldReturn401WhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/v1/entry-tickets"))
                .andExpect(status().isUnauthorized());

        verify(getUseCase, never()).getAll();
    }



    @Test
    @DisplayName("OPERARIO: Debe denegar la consulta de todos los tickets de entrada (403)")
    void shouldDenyGetAllEntryTicketsForOperario() throws Exception {
        mockMvc.perform(get("/v1/entry-tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERARIO"))))
                .andExpect(status().isForbidden());

        verify(getUseCase, never()).getAll();
    }

    @Test
    @DisplayName("OPERARIO: Debe permitir obtener un ticket de entrada por codigo (200)")
    void shouldAllowGetEntryTicketByCodeForOperario() throws Exception {
        UUID id = UUID.randomUUID();
        when(getUseCase.execute(id))
                .thenReturn(new EntryTicketDTO(id, UUID.randomUUID(), Instant.now(), "XYZ789"));

        mockMvc.perform(get("/v1/entry-tickets/{id}/code", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERARIO"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("OPERARIO: Debe permitir crear un ticket de entrada (201)")
    void shouldAllowCreateEntryTicketForOperario() throws Exception {
        UUID stayId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        when(createUseCase.execute(any()))
                .thenReturn(new EntryTicketDTO(ticketId, stayId, Instant.now(), "CODE01"));

        mockMvc.perform(post("/v1/entry-tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERARIO")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + stayId + "\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("OPERARIO: Debe denegar la actualizacion de un ticket de entrada (403)")
    void shouldDenyUpdateEntryTicketForOperario() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(put("/v1/entry-tickets/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERARIO")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isForbidden());
    }



    @Test
    @DisplayName("USER: Debe denegar la consulta de todos los tickets de entrada (403)")
    void shouldDenyGetAllEntryTicketsForUser() throws Exception {
        mockMvc.perform(get("/v1/entry-tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());

        verify(getUseCase, never()).getAll();
    }

    @Test
    @DisplayName("USER: Debe permitir obtener un ticket de entrada por codigo (200)")
    void shouldAllowGetEntryTicketByCodeForUser() throws Exception {
        UUID id = UUID.randomUUID();
        when(getUseCase.execute(id))
                .thenReturn(new EntryTicketDTO(id, UUID.randomUUID(), Instant.now(), "XYZ789"));

        mockMvc.perform(get("/v1/entry-tickets/{id}/code", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER: Debe denegar la creacion de un ticket de entrada (403)")
    void shouldDenyCreateEntryTicketForUser() throws Exception {
        UUID stayId = UUID.randomUUID();
        mockMvc.perform(post("/v1/entry-tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + stayId + "\"}"))
                .andExpect(status().isForbidden());

        verify(createUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("USER: Debe denegar la actualizacion de un ticket de entrada (403)")
    void shouldDenyUpdateEntryTicketForUser() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(put("/v1/entry-tickets/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isForbidden());
    }
}