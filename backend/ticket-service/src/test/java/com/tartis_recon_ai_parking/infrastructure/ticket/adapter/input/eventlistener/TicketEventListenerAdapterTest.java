package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketAlreadyExistsException;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener.dto.EntryTicketOfflineEventDto;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener.dto.StayClosedEvent;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener.dto.StayClosedEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
        Instant exitDate = Instant.now();
        StayClosedEventData data = new StayClosedEventData(
                stayId,
                exitDate,
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
        assertEquals(exitDate, capturedDto.issuedAt());
    }

    @Test
    void handleStayClosedEvent_whenUseCaseThrowsException_shouldPropagateException() throws InvalidTicketException {
        // Arrange
        StayClosedEventData data = new StayClosedEventData(UUID.randomUUID(), Instant.now(), BigDecimal.TEN);
        StayClosedEvent event = new StayClosedEvent(UUID.randomUUID(), "StayClosedEvent", "v1", Instant.now(), data);
        
        doThrow(new InvalidTicketException("Ticket is invalid"))
                .when(createTicketUseCase).execute(any(TicketCreateDTO.class));

        // Act & Assert
        assertThrows(InvalidTicketException.class, () -> adapter.handleStayClosedEvent(event));
    }

    @Test
    void handleStayClosedEvent_whenDataIsNull_shouldThrowNullPointerException() {
        // Arrange
        StayClosedEvent event = new StayClosedEvent(UUID.randomUUID(), "StayClosedEvent", "v1", Instant.now(), null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> adapter.handleStayClosedEvent(event));
    }

    @Test
    void handleStayClosedEvent_whenDataIntegrityViolationExceptionForStayId_shouldIgnore() throws InvalidTicketException {
        // Arrange
        StayClosedEventData data = new StayClosedEventData(UUID.randomUUID(), Instant.now(), BigDecimal.TEN);
        StayClosedEvent event = new StayClosedEvent(UUID.randomUUID(), "StayClosedEvent", "v1", Instant.now(), data);
        
        doThrow(new DataIntegrityViolationException("Duplicate key value violates unique constraint uk_stay_id"))
                .when(createTicketUseCase).execute(any(TicketCreateDTO.class));

        // Act & Assert
        // Should not throw any exception when it's a stay_id constraint violation
        adapter.handleStayClosedEvent(event);
        verify(createTicketUseCase).execute(any(TicketCreateDTO.class));
    }

    @Test
    void handleStayClosedEvent_whenOtherDataIntegrityViolationException_shouldRethrow() throws InvalidTicketException {
        // Arrange
        StayClosedEventData data = new StayClosedEventData(UUID.randomUUID(), Instant.now(), BigDecimal.TEN);
        StayClosedEvent event = new StayClosedEvent(UUID.randomUUID(), "StayClosedEvent", "v1", Instant.now(), data);
        
        doThrow(new DataIntegrityViolationException("NOT NULL constraint violation on column total_amount"))
                .when(createTicketUseCase).execute(any(TicketCreateDTO.class));

        // Act & Assert
        // Should rethrow DataIntegrityViolationException for unhandled DB integrity errors
        assertThrows(DataIntegrityViolationException.class, () -> adapter.handleStayClosedEvent(event));
    }

    @Test
    void handleStayClosedEvent_whenTicketAlreadyExistsException_shouldIgnore() throws InvalidTicketException {
        // Arrange
        StayClosedEventData data = new StayClosedEventData(UUID.randomUUID(), Instant.now(), BigDecimal.TEN);
        StayClosedEvent event = new StayClosedEvent(UUID.randomUUID(), "StayClosedEvent", "v1", Instant.now(), data);
        
        doThrow(new com.tartis_recon_ai_parking.domain.ticket.exception.TicketAlreadyExistsException("Already exists"))
                .when(createTicketUseCase).execute(any(TicketCreateDTO.class));

        // Act & Assert (Idempotencia: se debe capturar e ignorar sin lanzar excepción hacia afuera)
        adapter.handleStayClosedEvent(event);
        verify(createTicketUseCase).execute(any(TicketCreateDTO.class));
    }

    // TESTS: handleEntryTicketOfflineEvent
    @Test
    void handleEntryTicketOfflineEvent_shouldCallUseCaseWithCorrectData() throws InvalidTicketException {
        // Arrange
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        EntryTicketOfflineEventDto event = new EntryTicketOfflineEventDto(
                stayId,
                "XYZ-9876",
                "OFFLINE-ENTRY-12345",
                issuedAt
        );

        // Act
        adapter.handleEntryTicketOfflineEvent(event);

        // Assert
        ArgumentCaptor<TicketCreateDTO> captor = ArgumentCaptor.forClass(TicketCreateDTO.class);
        verify(createTicketUseCase).execute(captor.capture());

        TicketCreateDTO capturedDto = captor.getValue();
        assertEquals(stayId, capturedDto.stayId());
        assertEquals(issuedAt, capturedDto.issuedAt());
        assertNull(capturedDto.totalAmount());
    }

    @Test
    void handleEntryTicketOfflineEvent_whenTicketAlreadyExistsException_shouldIgnore() throws InvalidTicketException {
        // Arrange
        EntryTicketOfflineEventDto event = new EntryTicketOfflineEventDto(
                UUID.randomUUID(),
                "XYZ-9876",
                "OFFLINE-ENTRY-12345",
                Instant.now()
        );

        doThrow(new TicketAlreadyExistsException("Already exists"))
                .when(createTicketUseCase).execute(any(TicketCreateDTO.class));

        // Act & Assert
        adapter.handleEntryTicketOfflineEvent(event);
        verify(createTicketUseCase).execute(any(TicketCreateDTO.class));
    }

    @Test
    void handleEntryTicketOfflineEvent_whenDataIntegrityViolationExceptionForStayId_shouldIgnore() throws InvalidTicketException {
        // Arrange
        EntryTicketOfflineEventDto event = new EntryTicketOfflineEventDto(
                UUID.randomUUID(),
                "XYZ-9876",
                "OFFLINE-ENTRY-12345",
                Instant.now()
        );

        doThrow(new DataIntegrityViolationException("Duplicate key value violates unique constraint uk_stay_id"))
                .when(createTicketUseCase).execute(any(TicketCreateDTO.class));

        // Act & Assert
        adapter.handleEntryTicketOfflineEvent(event);
        verify(createTicketUseCase).execute(any(TicketCreateDTO.class));
    }

    @Test
    void handleEntryTicketOfflineEvent_whenOtherDataIntegrityViolationException_shouldRethrow() throws InvalidTicketException {
        // Arrange
        EntryTicketOfflineEventDto event = new EntryTicketOfflineEventDto(
                UUID.randomUUID(),
                "XYZ-9876",
                "OFFLINE-ENTRY-12345",
                Instant.now()
        );

        doThrow(new DataIntegrityViolationException("NOT NULL constraint violation"))
                .when(createTicketUseCase).execute(any(TicketCreateDTO.class));

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> adapter.handleEntryTicketOfflineEvent(event));
    }
}
