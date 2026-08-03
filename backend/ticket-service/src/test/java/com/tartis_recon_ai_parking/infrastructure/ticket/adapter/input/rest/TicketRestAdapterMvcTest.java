package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.application.ticket.usecase.GetTicketUseCase;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketNotFoundException;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketRestAdapter.class)
@Import({TicketRestMapperImpl.class, SecurityConfig.class})
class TicketRestAdapterMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateTicketUseCase createUseCase;

    @MockitoBean
    private GetTicketUseCase getTicketUseCase;

    @Test
    @DisplayName("POST /v1/tickets deberia devolver 201 con el ticket creado")
    void createTicket_shouldReturn201() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();

        TicketDTO savedDto = TicketDTO.builder()
                .uniqueId(ticketId)
                .stayId(stayId)
                .issuedAt(issuedAt)
                .totalAmount(BigDecimal.ZERO)
                .build();

        when(createUseCase.execute(any())).thenReturn(savedDto);

        mockMvc.perform(post("/v1/tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + stayId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uniqueId").value(ticketId.toString()))
                .andExpect(jsonPath("$.stayId").value(stayId.toString()));
    }

    @Test
    @DisplayName("GET /v1/tickets deberia devolver 200 con la lista de tickets")
    void listTickets_shouldReturn200() throws Exception {
        when(getTicketUseCase.getPage(any(), anyInt(), anyInt()))
                .thenReturn(new GetTicketUseCase.TicketPageDTO(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/v1/tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(getTicketUseCase, times(1)).getPage(any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /v1/tickets/{id} deberia devolver 200 cuando existe")
    void getById_shouldReturn200WhenExists() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();

        TicketDTO dto = TicketDTO.builder()
                .uniqueId(ticketId)
                .stayId(stayId)
                .issuedAt(Instant.now())
                .totalAmount(BigDecimal.valueOf(25.50))
                .build();

        when(getTicketUseCase.getById(ticketId)).thenReturn(dto);

        mockMvc.perform(get("/v1/tickets/{id}", ticketId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueId").value(ticketId.toString()))
                .andExpect(jsonPath("$.stayId").value(stayId.toString()))
                .andExpect(jsonPath("$.totalAmount").value(25.50));
    }

    @Test
    @DisplayName("GET /v1/tickets/{id} deberia devolver 404 cuando no existe")
    void getById_shouldReturn404WhenNotFound() throws Exception {
        UUID ticketId = UUID.randomUUID();
        when(getTicketUseCase.getById(ticketId))
                .thenThrow(new TicketNotFoundException("No existe un ticket con id " + ticketId));

        mockMvc.perform(get("/v1/tickets/{id}", ticketId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /v1/tickets deberia devolver 400 con body vacio")
    void createTicket_shouldReturn400WithEmptyBody() throws Exception {
        mockMvc.perform(post("/v1/tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /v1/tickets/{id} deberia devolver 400 con id invalido")
    void getById_shouldReturn400WithInvalidUuid() throws Exception {
        mockMvc.perform(get("/v1/tickets/{id}", "not-a-uuid")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isBadRequest());
    }

    // --- SEC-04: verificacion del resource server ---

    @Test
    @DisplayName("Debe rechazar con 401 GET /v1/tickets sin token")
    void shouldReturn401WhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/v1/tickets"))
                .andExpect(status().isUnauthorized());

        verify(getTicketUseCase, never()).getPage(any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Debe rechazar con 401 POST /v1/tickets sin token")
    void shouldReturn401OnCreateTicketWithoutToken() throws Exception {
        mockMvc.perform(post("/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isUnauthorized());

        verify(createUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Debe rechazar con 401 GET /v1/tickets/{id} sin token")
    void shouldReturn401OnGetTicketByIdWithoutToken() throws Exception {
        mockMvc.perform(get("/v1/tickets/{id}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());

        verify(getTicketUseCase, never()).getById(any());
    }

    // =========================================================================
    // SEC-10: pruebas de autorizacion fina para el rol OPERARIO
    // Matriz SEC-03:  TK-01 ✅  TK-02 ❌  TK-03 ✅
    // =========================================================================

    @Test
    @DisplayName("OPERARIO: Debe permitir crear un ticket de salida (201)")
    void shouldAllowCreateTicketForOperario() throws Exception {
        UUID ticketId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        when(createUseCase.execute(any())).thenReturn(TicketDTO.builder()
                .uniqueId(ticketId).stayId(stayId)
                .issuedAt(Instant.now()).totalAmount(BigDecimal.ZERO).build());

        mockMvc.perform(post("/v1/tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERARIO")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + stayId + "\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("OPERARIO: Debe denegar la consulta de todos los tickets (403)")
    void shouldDenyListTicketsForOperario() throws Exception {
        mockMvc.perform(get("/v1/tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERARIO"))))
                .andExpect(status().isForbidden());

        verify(getTicketUseCase, never()).getPage(any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("OPERARIO: Debe permitir consultar un ticket por id (200)")
    void shouldAllowGetByIdForOperario() throws Exception {
        UUID ticketId = UUID.randomUUID();
        when(getTicketUseCase.getById(ticketId)).thenReturn(TicketDTO.builder()
                .uniqueId(ticketId).stayId(UUID.randomUUID())
                .issuedAt(Instant.now()).totalAmount(BigDecimal.ZERO).build());

        mockMvc.perform(get("/v1/tickets/{id}", ticketId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OPERARIO"))))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // SEC-10: pruebas de autorizacion fina para el rol USER
    // Matriz SEC-03:  TK-01 ❌  TK-02 ❌  TK-03 ✅
    // =========================================================================

    @Test
    @DisplayName("USER: Debe denegar la creacion de un ticket de salida (403)")
    void shouldDenyCreateTicketForUser() throws Exception {
        UUID stayId = UUID.randomUUID();
        mockMvc.perform(post("/v1/tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stayId\":\"" + stayId + "\"}"))
                .andExpect(status().isForbidden());

        verify(createUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("USER: Debe denegar la consulta de todos los tickets (403)")
    void shouldDenyListTicketsForUser() throws Exception {
        mockMvc.perform(get("/v1/tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden());

        verify(getTicketUseCase, never()).getPage(any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("USER: Debe permitir consultar un ticket por id (200)")
    void shouldAllowGetByIdForUser() throws Exception {
        UUID ticketId = UUID.randomUUID();
        when(getTicketUseCase.getById(ticketId)).thenReturn(TicketDTO.builder()
                .uniqueId(ticketId).stayId(UUID.randomUUID())
                .issuedAt(Instant.now()).totalAmount(BigDecimal.ZERO).build());

        mockMvc.perform(get("/v1/tickets/{id}", ticketId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk());
    }
}
