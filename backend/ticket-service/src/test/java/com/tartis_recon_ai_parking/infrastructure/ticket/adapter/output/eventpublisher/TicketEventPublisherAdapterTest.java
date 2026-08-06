package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.eventpublisher;

import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;

@ExtendWith(MockitoExtension.class)
class TicketEventPublisherAdapterTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private TicketEventPublisherAdapter adapter;

    @Test
    void publish_shouldPublishEventToApplicationEventPublisher() {
        EntryTicket ticket = EntryTicket.create(UUID.randomUUID());
        TicketChangedEvent event = TicketChangedEvent.of(ticket, Instant.now());

        adapter.publish(event);

        verify(applicationEventPublisher).publishEvent(event);
    }
}

