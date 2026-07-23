package com.tartis_recon_ai_parking.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.application.ticket.usecase.GetTicketUseCase;

@Configuration
public class BeanConfiguration {
     @Bean
    public GetTicketUseCase getSpotUseCase(TicketPersistence persistence) {
        return new GetTicketUseCase(persistence);
    }

}
