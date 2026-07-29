package com.tartis_recon_ai_parking.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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

    @Bean
    public DirectExchange parkingEventsExchange() {
        return new DirectExchange("parking-events-exchange");
    }

    @Bean
    public Queue ticketStayClosedQueue() {
        return new Queue("ticket-service-stay-closed-queue");
    }

    @Bean
    public Binding bindingTicketStayClosedQueue(Queue ticketStayClosedQueue, DirectExchange parkingEventsExchange) {
        return BindingBuilder.bind(ticketStayClosedQueue)
                .to(parkingEventsExchange)
                .with("stay-closed-v1");
    }
}
