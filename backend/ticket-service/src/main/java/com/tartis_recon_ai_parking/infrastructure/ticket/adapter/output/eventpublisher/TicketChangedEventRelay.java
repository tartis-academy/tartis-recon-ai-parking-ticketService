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
 * Los use cases publican TicketChangedEvent como evento de aplicacion dentro de
 * su @Transactional; este listener lo reenvia a RabbitMQ solo si la transaccion
 * hace commit, evitando publicar un cambio que finalmente no quedo persistido.
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
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_TICKET_CHANGED,
                event
            );
        } catch (RuntimeException e) {
            // La transaccion ya commiteo: propagar solo romperia el hilo del
            // llamante por una publicacion que es best-effort.
            log.error("No se pudo publicar el evento {} de cambio a RabbitMQ para el ticket {}",
                    event.eventId(), event.data().ticketId(), e);
        }
    }
}

