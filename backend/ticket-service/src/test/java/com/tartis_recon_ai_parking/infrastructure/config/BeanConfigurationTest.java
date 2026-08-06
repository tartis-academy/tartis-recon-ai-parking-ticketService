package com.tartis_recon_ai_parking.infrastructure.config;

import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.application.entryticket.usecase.CreateEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.entryticket.usecase.GetEntryTicketUseCase;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketEventPublisher;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.application.ticket.usecase.GetTicketUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BeanConfigurationTest {

    @Mock
    private TicketPersistence ticketPersistence;

    @Mock
    private EntryTicketPersistence entryTicketPersistence;

    @Mock
    private TicketEventPublisher ticketEventPublisher;

    private final BeanConfiguration config = new BeanConfiguration();

    @Test
    void getSpotUseCase_shouldReturnGetTicketUseCaseInstance() {
        GetTicketUseCase useCase = config.getSpotUseCase(ticketPersistence);
        assertThat(useCase).isNotNull().isInstanceOf(GetTicketUseCase.class);
    }

    @Test
    void createEntryTicketUseCase_shouldReturnInstance() {
        CreateEntryTicketUseCase useCase = config.createEntryTicketUseCase(entryTicketPersistence, ticketEventPublisher);
        assertThat(useCase).isNotNull().isInstanceOf(CreateEntryTicketUseCase.class);
    }

    @Test
    void getEntryTicketUseCase_shouldReturnInstance() {
        GetEntryTicketUseCase useCase = config.getEntryTicketUseCase(entryTicketPersistence);
        assertThat(useCase).isNotNull().isInstanceOf(GetEntryTicketUseCase.class);
    }

    @Test
    void createTicketUseCase_shouldReturnInstance() {
        CreateTicketUseCase useCase = config.createTicketUseCase(ticketPersistence, ticketEventPublisher);
        assertThat(useCase).isNotNull().isInstanceOf(CreateTicketUseCase.class);
    }
}
