package com.tartis_recon_ai_parking.ticket_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener.TicketEventListenerAdapter;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/parking",
        "spring.rabbitmq.listener.simple.auto-startup=false"
})
class TicketServiceApplicationTests {

    @MockitoBean
    private TicketEventListenerAdapter ticketEventListenerAdapter;

    @Test
	void contextLoads() {
	}

}
