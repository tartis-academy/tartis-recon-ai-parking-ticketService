package com.tartis_recon_ai_parking.infrastructure.customizedexception.adapter.output;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;

@RestControllerAdvice
public class CustomizedExceptionAdapter {

    @ExceptionHandler(EntryTicketNotFoundException.class)
    public ProblemDetail handleNotFound(EntryTicketNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso no encontrado");
        return problem;
    }

    @ExceptionHandler(InvalidEntryTicketException.class)
    public ProblemDetail handleInvalid(InvalidEntryTicketException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Datos de ticket no válidos");
        return problem;
    }
}