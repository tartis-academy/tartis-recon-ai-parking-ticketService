package com.tartis_recon_ai_parking.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.application.entryticket.usecase.CreateEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.usecase.GetEntryTicketUseCase;

@Configuration
public class BeanConfiguration {

    @Bean
    CreateEntryTicketUseCase createEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence){
        return new CreateEntryTicketUseCase(entryTicketPersistence);
    }

    @Bean
    GetEntryTicketUseCase getEntryTicketUseCase(EntryTicketPersistence entryTicketPersistence){
        return new GetEntryTicketUseCase(entryTicketPersistence);
    }

}