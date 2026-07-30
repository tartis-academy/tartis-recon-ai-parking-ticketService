package com.tartis_recon_ai_parking.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.application.ticket.usecase.GetTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.application.entryticket.usecase.CreateEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.usecase.GetEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.usecase.UpdateEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;

@Configuration
public class BeanConfiguration {
     @Bean
    public GetTicketUseCase getSpotUseCase(TicketPersistence persistence) {
        return new GetTicketUseCase(persistence);
    }

    @Bean
    CreateEntryTicketUseCase createEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence){
        return new CreateEntryTicketUseCase(entryTicketPersistence);
    }

    @Bean
    GetEntryTicketUseCase getEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence){
        return new GetEntryTicketUseCase(entryTicketPersistence);
    }

    @Bean
    UpdateEntryTicketUseCase updateEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence){
        return new UpdateEntryTicketUseCase(entryTicketPersistence);
    }

    @Bean
    CreateTicketUseCase createTicketUseCase(TicketPersistence ticketPersistence){
        return new CreateTicketUseCase(ticketPersistence);
    }

}

