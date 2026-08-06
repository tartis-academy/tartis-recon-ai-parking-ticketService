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
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "parking-events-exchange";
    public static final String ROUTING_KEY_TICKET_CHANGED = "ticket-changed-v1";

    public static final String STAY_CLOSED_QUEUE = "ticket-service-stay-closed-queue";
    public static final String DLX_EXCHANGE = "ticket-service-stay-closed-dlx";
    public static final String DLQ_ROUTING_KEY = "ticket-service-stay-closed-dead-letter";
    public static final String DLQ_NAME = "ticket-service-stay-closed-dlq";

    // --- ENTRY TICKET OFFLINE QUEUE (RES-07) ---
    public static final String ENTRY_TICKET_OFFLINE_QUEUE = "ticket-service-entry-ticket-offline-queue";
    public static final String ENTRY_TICKET_OFFLINE_ROUTING_KEY = "entry-ticket-offline-v1";
    public static final String ENTRY_TICKET_OFFLINE_DLX = "ticket-service-entry-ticket-offline-dlx";
    public static final String ENTRY_TICKET_OFFLINE_DLQ_ROUTING_KEY = "ticket-service-entry-ticket-offline-dead-letter";
    public static final String ENTRY_TICKET_OFFLINE_DLQ = "ticket-service-entry-ticket-offline-dlq";

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
    // Ignora campos extra enviados por los productores sin romper la deserialización
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // Soporte correcto para tipos java.time (Instant, LocalDateTime)
    objectMapper.registerModule(new JavaTimeModule());
    
    return new Jackson2JsonMessageConverter(objectMapper);
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

    // CONFIGURACIÓN DE COLA ENTRY TICKET OFFLINE (RES-07)
    // =========================================================================

    @Bean
    public Queue entryTicketOfflineQueue() {
        return QueueBuilder.durable(ENTRY_TICKET_OFFLINE_QUEUE)
                .withArgument("x-dead-letter-exchange", ENTRY_TICKET_OFFLINE_DLX)
                .withArgument("x-dead-letter-routing-key", ENTRY_TICKET_OFFLINE_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding bindingEntryTicketOfflineQueue(Queue entryTicketOfflineQueue, TopicExchange parkingEventsExchange) {
        return BindingBuilder.bind(entryTicketOfflineQueue)
                .to(parkingEventsExchange)
                .with(ENTRY_TICKET_OFFLINE_ROUTING_KEY);
    }

    @Bean
    public TopicExchange entryTicketOfflineDLX() {
        return new TopicExchange(ENTRY_TICKET_OFFLINE_DLX);
    }

    @Bean
    public Queue entryTicketOfflineDLQ() {
        return QueueBuilder.durable(ENTRY_TICKET_OFFLINE_DLQ).build();
    }

    @Bean
    public Binding bindingEntryTicketOfflineDLQ(Queue entryTicketOfflineDLQ, TopicExchange entryTicketOfflineDLX) {
        return BindingBuilder.bind(entryTicketOfflineDLQ)
                .to(entryTicketOfflineDLX)
                .with(ENTRY_TICKET_OFFLINE_DLQ_ROUTING_KEY);
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, DLX_EXCHANGE, DLQ_ROUTING_KEY);
    }

    @Bean
    public UnroutableEventLogger unroutableEventLogger() {
        return new UnroutableEventLogger();
    }

    /**
     * Engancha el callback de mensajes devueltos al RabbitTemplate autoconfigurado.
     *
     * <p>Las otras dos piezas viven en application.properties, que es donde alguien
     * las va a buscar: {@code spring.rabbitmq.template.mandatory} y
     * {@code spring.rabbitmq.publisher-returns}. Sin esta ultima el callback no se
     * ejecuta aunque este registrado aqui. Ver {@link UnroutableEventLogger}.
     */
    @Bean
    public RabbitTemplateCustomizer returnsCallbackCustomizer(UnroutableEventLogger unroutableEventLogger) {
        return rabbitTemplate -> rabbitTemplate.setReturnsCallback(unroutableEventLogger);
    }
}
