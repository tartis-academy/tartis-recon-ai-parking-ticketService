package com.tartis_recon_ai_parking.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "parking-events-exchange";
    public static final String ROUTING_KEY_TICKET_CHANGED = "ticket-changed-v1";

    public static final String STAY_CLOSED_QUEUE = "ticket-service-stay-closed-queue";
    public static final String DLX_EXCHANGE = "ticket-service-stay-closed-dlx";
    public static final String DLQ_ROUTING_KEY = "ticket-service-stay-closed-dead-letter";
    public static final String DLQ_NAME = "ticket-service-stay-closed-dlq";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange parkingEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue ticketStayClosedQueue() {
        return QueueBuilder.durable(STAY_CLOSED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding bindingTicketStayClosedQueue(Queue ticketStayClosedQueue, TopicExchange parkingEventsExchange) {
        return BindingBuilder.bind(ticketStayClosedQueue)
                .to(parkingEventsExchange)
                .with("stay-closed-v1");
    }

    // =========================================================================
    // DEAD LETTER QUEUE (DLQ) & EXCHANGE (DLX) PARA CONSUMIDOR TICKET SERVICE
    // =========================================================================
    @Bean
    public TopicExchange ticketStayClosedDLX() {
        return new TopicExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue ticketStayClosedDLQ() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    @Bean
    public Binding bindingTicketStayClosedDLQ(Queue ticketStayClosedDLQ, TopicExchange ticketStayClosedDLX) {
        return BindingBuilder.bind(ticketStayClosedDLQ)
                .to(ticketStayClosedDLX)
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, DLX_EXCHANGE, DLQ_ROUTING_KEY);
    }

    /**
     * Activa mandatory y engancha el callback de mensajes devueltos, para que un
     * evento publicado sin ninguna cola que lo recoja deje una linea de ERROR en
     * vez de desaparecer. Ver {@link UnroutableEventLogger}.
     */
    @Bean
    public UnroutableEventLogger unroutableEventLogger(RabbitTemplate rabbitTemplate) {
        UnroutableEventLogger callback = new UnroutableEventLogger();
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(callback);
        return callback;
    }
}
