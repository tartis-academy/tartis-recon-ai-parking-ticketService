package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener.dto.StayClosedEvent;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener.dto.StayClosedEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TicketEventListenerAdapterTest {

    private CreateTicketUseCase createTicketUseCase;
    private TicketEventListenerAdapter adapter;

    @BeforeEach
    void setUp() {
        createTicketUseCase = mock(CreateTicketUseCase.class);
        adapter = new TicketEventListenerAdapter(createTicketUseCase);
    }

    @Test
    void handleStayClosedEvent_shouldCallUseCaseWithCorrectData() throws InvalidTicketException {
        // Arrange
        UUID stayId = UUID.randomUUID();
        BigDecimal totalAmount = new BigDecimal("15.50");
        StayClosedEventData data = new StayClosedEventData(
                stayId,
                "1234ABC",
                "A-12",
                Instant.now(),
                Instant.now(),
                totalAmount
        );
        StayClosedEvent event = new StayClosedEvent(
                UUID.randomUUID(),
                "StayClosedEvent",
                "v1",
                Instant.now(),
                data
        );

        // Act
        adapter.handleStayClosedEvent(event);

        // Assert
        ArgumentCaptor<TicketCreateDTO> captor = ArgumentCaptor.forClass(TicketCreateDTO.class);
        verify(createTicketUseCase).execute(captor.capture());

        TicketCreateDTO capturedDto = captor.getValue();
        assertEquals(stayId, capturedDto.stayId());
        assertEquals(totalAmount, capturedDto.totalAmount());
        // issuedAt is Instant.now(), we just verify it's not null
        assertEquals(true, capturedDto.issuedAt() != null);
    }
}
