package com.tartis_recon_ai_parking.application.ticket.port.output;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;

public interface TicketEventPublisher {
    void publish(TicketChangedEvent event);
}
