package com.tartis_recon_ai_parking.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.application.entryticket.usecase.CreateEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.usecase.GetEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.usecase.UpdateEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketEventPublisher;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.application.ticket.usecase.GetTicketUseCase;

@Configuration
public class BeanConfiguration {

    @Bean
    public GetTicketUseCase getSpotUseCase(TicketPersistence persistence) {
        return new GetTicketUseCase(persistence);
    }

    @Bean
    public CreateEntryTicketUseCase createEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence, TicketEventPublisher ticketEventPublisher) {
        return new CreateEntryTicketUseCase(entryTicketPersistence, ticketEventPublisher);
    }

    @Bean
    public GetEntryTicketUseCase getEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence) {
        return new GetEntryTicketUseCase(entryTicketPersistence);
    }

    @Bean
    public UpdateEntryTicketUseCase updateEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence, TicketEventPublisher ticketEventPublisher) {
        return new UpdateEntryTicketUseCase(entryTicketPersistence, ticketEventPublisher);
    }

    @Bean
    public CreateTicketUseCase createTicketUseCase(TicketPersistence ticketPersistence, TicketEventPublisher ticketEventPublisher) {
        return new CreateTicketUseCase(ticketPersistence, ticketEventPublisher);
    }
}
