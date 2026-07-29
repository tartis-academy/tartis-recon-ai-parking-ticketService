package com.tartis_recon_ai_parking.infrastructure.customizedexception.adapter.output;

import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.application.ticket.usecase.GetTicketUseCase;
import com.tartis_recon_ai_parking.infrastructure.config.SecurityConfig;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.TicketRestAdapter;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.TicketRestMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketRestAdapter.class)
@Import({CustomizedExceptionAdapter.class, TicketRestMapperImpl.class, SecurityConfig.class})
class CustomizedExceptionAdapterMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateTicketUseCase createUseCase;

    @MockitoBean
    private GetTicketUseCase getTicketUseCase;

    @Test
    @DisplayName("Debe capturar DataIntegrityViolationException via @RestControllerAdvice y responder HTTP 409 Conflict")
    void shouldReturn409WhenDataIntegrityViolationOccurs() throws Exception {
        UUID id = UUID.randomUUID();
        when(getTicketUseCase.getById(any()))
                .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));

        mockMvc.perform(get("/v1/tickets/" + id)
                        .with(jwt()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("CONFLICT"))
                .andExpect(jsonPath("$.message").value("The operation violates database constraints or uniqueness requirements."));
    }

    @Test
    @DisplayName("Debe capturar DataAccessResourceFailureException via @RestControllerAdvice y responder HTTP 503 Service Unavailable")
    void shouldReturn503WhenDatabaseUnreachable() throws Exception {
        UUID id = UUID.randomUUID();
        when(getTicketUseCase.getById(any()))
                .thenThrow(new DataAccessResourceFailureException("Database unreachable"));

        mockMvc.perform(get("/v1/tickets/" + id)
                        .with(jwt()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.error").value("SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("The database is unreachable or the operation timed out. Please try again later."));
    }

    @Test
    @DisplayName("Debe capturar DataAccessException genérica via @RestControllerAdvice y responder HTTP 500 Internal Server Error sin expor detalles")
    void shouldReturn500WhenGenericDatabaseErrorOccurs() throws Exception {
        UUID id = UUID.randomUUID();
        when(getTicketUseCase.getById(any()))
                .thenThrow(new DataAccessException("SQL syntax error near SELECT") {});

        mockMvc.perform(get("/v1/tickets/" + id)
                        .with(jwt()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected database error occurred. The request could not be processed."));
    }
}

