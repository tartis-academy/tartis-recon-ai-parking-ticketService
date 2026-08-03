package com.tartis_recon_ai_parking.infrastructure.config;

import com.tartis_recon_ai_parking.application.entryticket.usecase.*;
import com.tartis_recon_ai_parking.application.ticket.usecase.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SEC-11: verifica que el exceptionHandling de SecurityConfig enruta correctamente
 * los errores 401 (sin token y con JWT invalido) y 403 (rol insuficiente) a traves del
 * HandlerExceptionResolver → CustomizedExceptionAdapter, produciendo un ErrorResponse
 * con la estructura definida en lugar de la respuesta por defecto de Spring.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:ticketdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/parking"
})
class SecurityConfigExceptionHandlingTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean private CreateTicketUseCase createTicketUseCase;
    @MockitoBean private GetTicketUseCase getTicketUseCase;
    @MockitoBean private CreateEntryTicketUseCase createEntryTicketUseCase;
    @MockitoBean private GetEntryTicketUseCase getEntryTicketUseCase;
    @MockitoBean private UpdateEntryTicketUseCase updateEntryTicketUseCase;
    @MockitoBean private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("SEC-11: Sin token, el authenticationEntryPoint enruta a CustomizedExceptionAdapter → 401 con ErrorResponse y cabecera WWW-Authenticate")
    void shouldReturn401WithErrorResponseWhenNoToken() throws Exception {
        mockMvc.perform(get("/v1/tickets"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, containsString("Bearer")))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Authentication token is missing, invalid, or expired."))
                .andExpect(jsonPath("$.path").value("/v1/tickets"));
    }

    @Test
    @DisplayName("SEC-11: Con JWT sintacticamente invalido, el resource server enruta a CustomizedExceptionAdapter → 401 con ErrorResponse y cabecera WWW-Authenticate")
    void shouldReturn401WithErrorResponseWhenMalformedJwt() throws Exception {
        when(jwtDecoder.decode(anyString())).thenThrow(new BadJwtException("Invalid or expired JWT"));

        mockMvc.perform(get("/v1/tickets")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, containsString("Bearer")))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("Authentication token is missing, invalid, or expired."))
                .andExpect(jsonPath("$.path").value("/v1/tickets"));
    }

    @Test
    @DisplayName("SEC-11: Con rol insuficiente, @PreAuthorize enruta a CustomizedExceptionAdapter → 403 con ErrorResponse")
    void shouldReturn403WithErrorResponseWhenInsufficientRole() throws Exception {
        mockMvc.perform(get("/v1/tickets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("You do not have permission to perform this action."))
                .andExpect(jsonPath("$.path").value("/v1/tickets"));
    }
}
