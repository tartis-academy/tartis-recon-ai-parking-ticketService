package com.tartis_recon_ai_parking.domain.ticket.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String message) { super(message); }
}