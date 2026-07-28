package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tartis_recon_ai_parking.domain.ticket.Ticket;

@ExtendWith(MockitoExtension.class)
class TicketPersistenceAdapterTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketPersistenceMapper ticketPersistenceMapper;

    @InjectMocks
    private TicketPersistenceAdapter adapter;

    private UUID ticketId;
    private UUID stayId;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();
        stayId = UUID.randomUUID();
    }

    @Test
    void save_ShouldMapPersistAndReturnDomainTicket() {
        Ticket ticket = Ticket.create(stayId, Instant.now(), BigDecimal.TEN);
        TicketEntity entityToSave = new TicketEntity();
        TicketEntity savedEntity = new TicketEntity();
        Ticket expectedDomain = Ticket.recreate(ticketId, stayId, Instant.now(), BigDecimal.TEN);

        when(ticketPersistenceMapper.toEntity(ticket)).thenReturn(entityToSave);
        when(ticketRepository.save(entityToSave)).thenReturn(savedEntity);
        when(ticketPersistenceMapper.toDomain(savedEntity)).thenReturn(expectedDomain);

        Ticket result = adapter.save(ticket);

        assertThat(result).isSameAs(expectedDomain);
        verify(ticketPersistenceMapper).toEntity(ticket);
        verify(ticketRepository).save(entityToSave);
        verify(ticketPersistenceMapper).toDomain(savedEntity);
    }

    @Test
    void findById_WhenTicketExists_ShouldReturnMappedDomainTicket() {
        TicketEntity entity = new TicketEntity();
        Ticket expectedDomain = Ticket.recreate(ticketId, stayId, Instant.now(), BigDecimal.ONE);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(entity));
        when(ticketPersistenceMapper.toDomain(entity)).thenReturn(expectedDomain);

        Optional<Ticket> result = adapter.findById(ticketId);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(expectedDomain);
    }

    @Test
    void findById_WhenTicketDoesNotExist_ShouldReturnEmptyOptional() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        Optional<Ticket> result = adapter.findById(ticketId);

        assertThat(result).isEmpty();
        verify(ticketPersistenceMapper, never()).toDomain(any());
    }
}
