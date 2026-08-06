package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.eventpublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketEventPublisher;

@Component
public class TicketEventPublisherAdapter implements TicketEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TicketEventPublisherAdapter.class);

    private final ApplicationEventPublisher eventPublisher;

    public TicketEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(TicketChangedEvent event) {
        log.info("Publicando evento interno TicketChangedEvent {} para ticket {}", event.eventId(), event.data().ticketId());
        eventPublisher.publishEvent(event);
    }
}

