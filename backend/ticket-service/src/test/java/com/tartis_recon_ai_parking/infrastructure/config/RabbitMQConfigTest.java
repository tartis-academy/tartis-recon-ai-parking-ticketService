package com.tartis_recon_ai_parking.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Cierra la regresion del incidente del exchange: ticket-service lo declaraba
 * como DirectExchange y stay-service como TopicExchange, con el mismo nombre.
 * RabbitMQ no deja redeclarar un exchange cambiandole el tipo, asi que el
 * segundo servicio en arrancar se llevaba un PRECONDITION_FAILED y se quedaba
 * sin publicar ni consumir — de forma no determinista, porque dependia del
 * orden de arranque, y sin que el healthcheck se enterara.
 *
 * <p>El tipo del bean es lo unico que impedia detectarlo sin levantar los dos
 * servicios a la vez, asi que se afirma aqui explicitamente. Los nombres y la
 * routing key tienen que seguir coincidiendo con los de RabbitMQConfig de
 * stay-service, que es quien publica.
 */
class RabbitMQConfigTest {

    private static final String EXCHANGE_NAME = "parking-events-exchange";
    private static final String TICKET_QUEUE = "ticket-service-stay-closed-queue";
    private static final String ROUTING_KEY = "stay-closed-v1";

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    void exchangeDebeSerTopicYNoDirect() {
        TopicExchange exchange = config.parkingEventsExchange();

        assertEquals(EXCHANGE_NAME, exchange.getName());
        assertEquals("topic", exchange.getType());
    }

    @Test
    void colaConElNombreQueEsperaStayServiceYArgumentosDLQ() {
        Queue queue = config.ticketStayClosedQueue();

        assertEquals(TICKET_QUEUE, queue.getName());
        assertEquals("ticket-service-stay-closed-dlx", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("ticket-service-stay-closed-dead-letter", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void bindingConLaRoutingKeyDelContratoDelEvento() {
        Queue queue = config.ticketStayClosedQueue();
        TopicExchange exchange = config.parkingEventsExchange();

        Binding binding = config.bindingTicketStayClosedQueue(queue, exchange);

        assertEquals(TICKET_QUEUE, binding.getDestination());
        assertEquals(EXCHANGE_NAME, binding.getExchange());
        assertEquals(ROUTING_KEY, binding.getRoutingKey());
    }

    @Test
    void debeConfigurarCorrectamenteInfraestructuraDLQ() {
        TopicExchange dlx = config.ticketStayClosedDLX();
        assertEquals("ticket-service-stay-closed-dlx", dlx.getName());

        Queue dlq = config.ticketStayClosedDLQ();
        assertEquals("ticket-service-stay-closed-dlq", dlq.getName());

        Binding dlqBinding = config.bindingTicketStayClosedDLQ(dlq, dlx);
        assertEquals("ticket-service-stay-closed-dlq", dlqBinding.getDestination());
        assertEquals("ticket-service-stay-closed-dlx", dlqBinding.getExchange());
        assertEquals("ticket-service-stay-closed-dead-letter", dlqBinding.getRoutingKey());

        org.springframework.amqp.rabbit.core.RabbitTemplate mockTemplate = org.mockito.Mockito.mock(org.springframework.amqp.rabbit.core.RabbitTemplate.class);
        org.junit.jupiter.api.Assertions.assertNotNull(config.messageRecoverer(mockTemplate));
    }
}
