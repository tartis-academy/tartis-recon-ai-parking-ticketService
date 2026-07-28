package com.tartis_recon_ai_parking.infrastructure.customizedexception.adapter.output;

import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class CustomizedExceptionAdapterTest {

    private final CustomizedExceptionAdapter adapter = new CustomizedExceptionAdapter();

    @Test
    void handleNotFound_shouldReturn404WithCorrectTitle() {
        EntryTicketNotFoundException ex = new EntryTicketNotFoundException("Ticket ABC not found");

        ProblemDetail result = adapter.handleNotFound(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Recurso no encontrado");
        assertThat(result.getDetail()).isEqualTo("Ticket ABC not found");
    }

    @Test
    void handleNotFound_shouldPreserveExceptionMessage() {
        String message = "EntryTicket not found: 123e4567-e89b-12d3-a456-426614174000";
        EntryTicketNotFoundException ex = new EntryTicketNotFoundException(message);

        ProblemDetail result = adapter.handleNotFound(ex);

        assertThat(result.getDetail()).isEqualTo(message);
    }

    @Test
    void handleInvalid_shouldReturn400WithCorrectTitle() {
        InvalidEntryTicketException ex = new InvalidEntryTicketException("stayId is null");

        ProblemDetail result = adapter.handleInvalid(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getTitle()).isEqualTo("Datos de ticket no válidos");
        assertThat(result.getDetail()).isEqualTo("stayId is null");
    }

    @Test
    void handleTicketNotFound_shouldReturn404WithCorrectTitle() {
        TicketNotFoundException ex = new TicketNotFoundException("Ticket 123 not found");

        ProblemDetail result = adapter.handleTicketNotFound(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Recurso no encontrado");
        assertThat(result.getDetail()).isEqualTo("Ticket 123 not found");
    }

    @Test
    void handleInvalidTicket_shouldReturn400WithCorrectTitle() {
        InvalidTicketException ex = new InvalidTicketException("stayId is null");

        ProblemDetail result = adapter.handleInvalidTicket(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getTitle()).isEqualTo("Datos de ticket no válidos");
        assertThat(result.getDetail()).isEqualTo("stayId is null");
    }
}
