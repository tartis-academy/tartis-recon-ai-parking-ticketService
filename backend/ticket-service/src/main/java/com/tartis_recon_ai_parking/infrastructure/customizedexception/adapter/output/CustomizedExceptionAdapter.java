package com.tartis_recon_ai_parking.infrastructure.customizedexception.adapter.output;

import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;
import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketNotFoundException;

// Punto unico de traduccion de excepciones a HTTP (IN-36), alineado con el schema ErrorResponse de openapi.yml
@RestControllerAdvice
public class CustomizedExceptionAdapter {

    private static final Logger log = LoggerFactory.getLogger(CustomizedExceptionAdapter.class);

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTicketNotFound(TicketNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidTicketException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTicket(InvalidTicketException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(EntryTicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntryTicketNotFound(EntryTicketNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidEntryTicketException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEntryTicket(InvalidEntryTicketException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(this::formatValidationError)
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Invalid request payload.";
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequest(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "The request body is missing, malformed, or contains a value that cannot be parsed (e.g. an unrecognized value for an enum field).",
                request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "expected type";
        String message = "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'. Expected " + requiredType + ".";
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception while processing request [{} {}]", request.getMethod(), request.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.", request);
    }

    private String formatValidationError(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            return fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }
        return error.getObjectName() + ": " + error.getDefaultMessage();
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(status.value(), status.name(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
