package com.tartis_recon_ai_parking.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Tiene que ser TopicExchange, no Direct: stay-service declara este mismo
    // exchange como Topic. RabbitMQ no deja redeclarar un exchange con un tipo
    // distinto, asi que el segundo servicio en arrancar se lleva un
    // PRECONDITION_FAILED ("inequivalent arg 'type'") y se queda sin publicar
    // ni consumir. Quien fallaba dependia del orden de arranque, y el servicio
    // seguia reportandose healthy: el fallo solo se veia leyendo el log entero.
    @Bean
    public TopicExchange parkingEventsExchange() {
        return new TopicExchange("parking-events-exchange");
    }

    @Bean
    public Queue ticketStayClosedQueue() {
        return new Queue("ticket-service-stay-closed-queue");
    }

    @Bean
    public Binding bindingTicketStayClosedQueue(Queue ticketStayClosedQueue, TopicExchange parkingEventsExchange) {
        return BindingBuilder.bind(ticketStayClosedQueue)
                .to(parkingEventsExchange)
                .with("stay-closed-v1");
    }
}
