package com.tartis_recon_ai_parking.infrastructure.customizedexception.adapter.output;

import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomizedExceptionAdapterTest {

    private final CustomizedExceptionAdapter exceptionAdapter = new CustomizedExceptionAdapter();

    private MockHttpServletRequest requestTo(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        return request;
    }

    @Test
    @DisplayName("Debe manejar TicketNotFoundException retornando 404 Not Found alineado con openapi.yml")
    void shouldHandleTicketNotFoundException() {
        UUID id = UUID.randomUUID();
        TicketNotFoundException exception = new TicketNotFoundException("No existe un ticket con id " + id);

        ResponseEntity<ErrorResponse> response =
                exceptionAdapter.handleTicketNotFound(exception, requestTo("/v1/tickets/" + id));

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("NOT_FOUND", response.getBody().getError());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
        assertEquals("/v1/tickets/" + id, response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Debe manejar InvalidTicketException retornando 400 Bad Request alineado con openapi.yml")
    void shouldHandleInvalidTicketException() {
        InvalidTicketException exception = new InvalidTicketException("stayId is null");

        ResponseEntity<ErrorResponse> response =
                exceptionAdapter.handleInvalidTicket(exception, requestTo("/v1/tickets"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("BAD_REQUEST", response.getBody().getError());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("Debe manejar EntryTicketNotFoundException retornando 404 Not Found")
    void shouldHandleEntryTicketNotFoundException() {
        UUID id = UUID.randomUUID();
        EntryTicketNotFoundException exception = new EntryTicketNotFoundException("EntryTicket not found: " + id);

        ResponseEntity<ErrorResponse> response =
                exceptionAdapter.handleEntryTicketNotFound(exception, requestTo("/v1/entry-tickets/" + id));

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("NOT_FOUND", response.getBody().getError());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
    }

    @Test
    @DisplayName("Debe manejar InvalidEntryTicketException retornando 400 Bad Request")
    void shouldHandleInvalidEntryTicketException() {
        InvalidEntryTicketException exception = new InvalidEntryTicketException("code is null");

        ResponseEntity<ErrorResponse> response =
                exceptionAdapter.handleInvalidEntryTicket(exception, requestTo("/v1/entry-tickets"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BAD_REQUEST", response.getBody().getError());
        assertEquals(exception.getMessage(), response.getBody().getMessage());
    }

    @Test
    @DisplayName("Debe manejar errores de validacion de @Valid devolviendo el detalle de los campos")
    void shouldHandleValidationErrors() throws NoSuchMethodException {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "ticketRequest");
        bindingResult.addError(new FieldError("ticketRequest", "stayId", "El stayId no puede ser nulo"));

        MethodParameter methodParameter = new MethodParameter(
                CustomizedExceptionAdapterTest.class.getDeclaredMethod("shouldHandleValidationErrors"), -1);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response =
                exceptionAdapter.handleValidation(exception, requestTo("/v1/tickets"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("stayId"));
        assertTrue(response.getBody().getMessage().contains("El stayId no puede ser nulo"));
    }

    @Test
    @DisplayName("Debe manejar errores de validacion a nivel de clase/objeto (ObjectError sin campo asociado), no solo FieldErrors")
    void shouldHandleClassLevelValidationErrors() throws NoSuchMethodException {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "ticketRequest");
        bindingResult.addError(new ObjectError("ticketRequest", "issuedAt must not be before entry time"));

        MethodParameter methodParameter = new MethodParameter(
                CustomizedExceptionAdapterTest.class.getDeclaredMethod("shouldHandleClassLevelValidationErrors"), -1);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response =
                exceptionAdapter.handleValidation(exception, requestTo("/v1/tickets"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("issuedAt must not be before entry time"));
    }

    @Test
    @DisplayName("Debe manejar un body JSON malformado o con un valor no reconocido devolviendo 400, no el catch-all de 500")
    void shouldHandleMalformedRequestBody() {
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(
                        "JSON parse error: Cannot deserialize value of type TicketStatus from String \"UNKNOWN\"",
                        (HttpInputMessage) null);

        ResponseEntity<ErrorResponse> response =
                exceptionAdapter.handleMalformedRequest(exception, requestTo("/v1/tickets"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BAD_REQUEST", response.getBody().getError());
        assertTrue(!response.getBody().getMessage().contains("Jackson"));
    }

    @Test
    @DisplayName("Debe manejar un path variable o query param con tipo incorrecto (ej. UUID mal formado) devolviendo 400, no el catch-all de 500")
    void shouldHandleTypeMismatch() throws NoSuchMethodException {
        MethodParameter methodParameter = new MethodParameter(
                CustomizedExceptionAdapterTest.class.getDeclaredMethod("shouldHandleTypeMismatch"), -1);
        MethodArgumentTypeMismatchException exception =
                new MethodArgumentTypeMismatchException("not-a-uuid", UUID.class, "id", methodParameter, new IllegalArgumentException());

        ResponseEntity<ErrorResponse> response =
                exceptionAdapter.handleTypeMismatch(exception, requestTo("/v1/tickets/not-a-uuid"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("id"));
        assertTrue(response.getBody().getMessage().contains("UUID"));
    }

    @Test
    @DisplayName("Debe manejar un query param obligatorio ausente devolviendo 400, no el catch-all de 500")
    void shouldHandleMissingParameter() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("stayId", "UUID");

        ResponseEntity<ErrorResponse> response =
                exceptionAdapter.handleMissingParameter(exception, requestTo("/v1/tickets"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BAD_REQUEST", response.getBody().getError());
    }

    @Test
    @DisplayName("Debe manejar cualquier excepcion no controlada devolviendo 500 sin exponer detalles internos")
    void shouldHandleUnexpectedException() {
        RuntimeException exception = new RuntimeException("connection refused by database driver XYZ");

        ResponseEntity<ErrorResponse> response =
                exceptionAdapter.handleUnexpected(exception, requestTo("/v1/tickets"));

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getError());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
        assertTrue(!response.getBody().getMessage().contains("database driver"));
    }
}
