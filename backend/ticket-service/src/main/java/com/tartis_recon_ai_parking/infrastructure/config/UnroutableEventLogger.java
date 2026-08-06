package com.tartis_recon_ai_parking.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Red de seguridad para lo que publica este servicio.
 *
 * <p>Publicar en un topic exchange sin ningun binding que case con la routing
 * key no deja rastro: RabbitMQ descarta el mensaje en silencio, no hay cola que
 * crezca, no hay DLQ y no hay log. Es peor que un consumidor caido, porque un
 * consumidor caido al menos acumula mensajes que alguien acaba viendo en la UI
 * de RabbitMQ.
 *
 * <p>Hoy nadie tiene una cola con binding a {@code ticket-changed-v1} (el
 * consumidor llega en un ticket posterior), asi que sin esto cada
 * TicketChangedEvent desaparece sin dejar constancia. Con {@code mandatory}
 * activado el broker devuelve al publicador todo mensaje que no haya podido
 * enrutar y este callback lo registra como ERROR.
 *
 * <p><b>Requiere ademas {@code spring.rabbitmq.publisher-returns=true}.</b> Sin
 * esa propiedad el CachingConnectionFactory no envuelve los canales en
 * PublisherCallbackChannel, RabbitTemplate.addListener() no registra nada y el
 * basic.return del broker se descarta en el cliente: este callback no llega a
 * ejecutarse nunca. Ambas propiedades estan en application.properties.
 *
 * <p>La cola y el binding se declaran SIEMPRE en el servicio que consume, nunca
 * aqui: declararlos en el publicador es lo que provoco el PRECONDITION_FAILED
 * de ASY-08.
 */
public class UnroutableEventLogger implements RabbitTemplate.ReturnsCallback {

    private static final Logger log = LoggerFactory.getLogger(UnroutableEventLogger.class);

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.error(
            "Mensaje devuelto por RabbitMQ y por tanto perdido: exchange={}, routingKey={}, replyCode={}, replyText={}. "
            + "Ninguna cola tiene un binding que case con esa routing key; el consumidor tiene que declararla en su servicio.",
            returned.getExchange(),
            returned.getRoutingKey(),
            returned.getReplyCode(),
            returned.getReplyText()
        );
    }
}
