package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.eventpublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.infrastructure.config.RabbitMQConfig;

@DataJpaTest
@Import({TicketChangedEventRelay.class, TicketEventPublisherAdapter.class})
class TicketChangedEventRelayTransactionalTest {

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void whenTransactionCommits_shouldPublishToRabbitMQ() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);

        EntryTicket ticket = EntryTicket.create(UUID.randomUUID());
        TicketChangedEvent event = TicketChangedEvent.of(ticket, Instant.now());

        template.executeWithoutResult(status -> {
            eventPublisher.publishEvent(event);
            verify(rabbitTemplate, never()).convertAndSend(any(), any(), any(Object.class));
        });

        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConfig.EXCHANGE_NAME),
            eq(RabbitMQConfig.ROUTING_KEY_TICKET_CHANGED),
            eq(event)
        );
    }

    @Test
    void whenTransactionRollsBack_shouldNotPublishToRabbitMQ() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);

        EntryTicket ticket = EntryTicket.create(UUID.randomUUID());
        TicketChangedEvent event = TicketChangedEvent.of(ticket, Instant.now());

        try {
            template.executeWithoutResult(status -> {
                eventPublisher.publishEvent(event);
                status.setRollbackOnly();
            });
        } catch (Exception ignored) {}

        verify(rabbitTemplate, never()).convertAndSend(any(), any(), any(Object.class));
    }
}
