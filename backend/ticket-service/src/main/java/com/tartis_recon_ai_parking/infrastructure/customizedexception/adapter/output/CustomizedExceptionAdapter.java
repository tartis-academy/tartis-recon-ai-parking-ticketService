package com.tartis_recon_ai_parking.infrastructure.customizedexception.adapter.output;

import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketAlreadyExistsException;
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

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import java.util.stream.Collectors;

// Punto unico de traduccion de excepciones a HTTP (IN-36), alineado con el schema ErrorResponse de openapi.yml
@RestControllerAdvice
public class CustomizedExceptionAdapter {

    private static final Logger log = LoggerFactory.getLogger(CustomizedExceptionAdapter.class);

    // Maneja el caso cuando no se encuentra un ticket solicitado (HTTP 404).
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTicketNotFound(TicketNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // Maneja errores de validación de datos en tickets (HTTP 400).
    @ExceptionHandler(InvalidTicketException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTicket(InvalidTicketException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Maneja el caso cuando un ticket ya existe para la estancia (HTTP 409).
    @ExceptionHandler(TicketAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleTicketAlreadyExists(TicketAlreadyExistsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // Maneja el caso cuando no se encuentra un ticket de entrada solicitado (HTTP 404).
    @ExceptionHandler(EntryTicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntryTicketNotFound(EntryTicketNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // Maneja errores de validación de datos en tickets de entrada (HTTP 400).
    @ExceptionHandler(InvalidEntryTicketException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEntryTicket(InvalidEntryTicketException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Agrupa y formatea los errores de validación de los campos de un DTO (@Valid) (HTTP 400).
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

    // Maneja peticiones HTTP con un cuerpo JSON malformado o ilegible (HTTP 400).
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequest(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "The request body is missing, malformed, or contains a value that cannot be parsed (e.g. an unrecognized value for an enum field).",
                request);
    }

    // Maneja errores por tipos de datos incorrectos en parámetros o variables de URL (HTTP 400).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "expected type";
        String message = "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'. Expected " + requiredType + ".";
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // Maneja la ausencia de un parámetro obligatorio en la petición HTTP (HTTP 400).
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Captura violaciones de restricciones en la base de datos, como registros duplicados (HTTP 409).
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT,
                "The operation violates database constraints or uniqueness requirements.", request);
    }

    // Maneja fallos de conexión a la base de datos o tiempos de espera agotados (HTTP 503).
    @ExceptionHandler({DataAccessResourceFailureException.class, QueryTimeoutException.class})
    public ResponseEntity<ErrorResponse> handleDatabaseTimeoutAndConnectionErrors(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE,
                "The database is unreachable or the operation timed out. Please try again later.", request);
    }


    // Captura cualquier otro error de base de datos ocultando detalles técnicos de la BD (HTTP 500).
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleGenericDatabaseException(Exception ex, HttpServletRequest request) {
        log.error("Database exception while processing request [{} {}]", request.getMethod(), request.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected database error occurred. The request could not be processed.", request);
    }

    // Formatea un error de validación de un campo u objeto a un texto entendible.
    private String formatValidationError(ObjectError error) {
        if (error instanceof FieldError fieldError) {
            return fieldError.getField() + ": " + fieldError.getDefaultMessage();
        }
        return error.getObjectName() + ": " + error.getDefaultMessage();
    }

    // Construye la respuesta HTTP ResponseEntity estandarizada con el ErrorResponse.
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(status.value(), status.name(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
