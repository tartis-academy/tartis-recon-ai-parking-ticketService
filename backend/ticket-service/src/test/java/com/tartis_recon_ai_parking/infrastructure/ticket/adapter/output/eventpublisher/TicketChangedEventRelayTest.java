package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.eventpublisher;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.infrastructure.config.RabbitMQConfig;

@ExtendWith(MockitoExtension.class)
class TicketChangedEventRelayTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TicketChangedEventRelay relay;

    @Test
    void onTicketChanged_shouldSendEventToRabbitMQ() {
        EntryTicket ticket = EntryTicket.create(UUID.randomUUID());
        TicketChangedEvent event = TicketChangedEvent.of(ticket, Instant.now());

        relay.onTicketChanged(event);

        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConfig.EXCHANGE_NAME),
            eq(RabbitMQConfig.ROUTING_KEY_TICKET_CHANGED),
            eq(event)
        );
    }

    @Test
    void onTicketChanged_shouldNotPropagateWhenBrokerFails() {
        EntryTicket ticket = EntryTicket.create(UUID.randomUUID());
        TicketChangedEvent event = TicketChangedEvent.of(ticket, Instant.now());
        doThrow(new AmqpConnectException(new RuntimeException("broker caido")))
            .when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        // En AFTER_COMMIT el ticket ya esta persistido: propagar solo romperia
        // el hilo del llamante por una publicacion best-effort.
        assertThatCode(() -> relay.onTicketChanged(event)).doesNotThrowAnyException();
    }
}
