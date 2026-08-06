package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.eventpublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;
import com.tartis_recon_ai_parking.infrastructure.config.RabbitMQConfig;

/**
 * Escucha los TicketChangedEvent de aplicacion y los reenvia a RabbitMQ
 * unicamente cuando la transaccion realiza commit exitoso (AFTER_COMMIT).
 */
@Component
public class TicketChangedEventRelay {

    private static final Logger log = LoggerFactory.getLogger(TicketChangedEventRelay.class);

    private final RabbitTemplate rabbitTemplate;

    public TicketChangedEventRelay(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTicketChanged(TicketChangedEvent event) {
        try {
            log.info("Publicando TicketChangedEvent {} a RabbitMQ para ticket {}", event.eventId(), event.data().ticketId());
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_TICKET_CHANGED,
                event
            );
        } catch (Exception e) {
            log.error("No se pudo publicar el evento {} de cambio a RabbitMQ para el ticket {}",
                    event.eventId(), event.data().ticketId(), e);
            throw new RuntimeException("Error al publicar TicketChangedEvent a RabbitMQ", e);
        }
    }
}

