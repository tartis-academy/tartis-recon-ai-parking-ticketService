package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TicketPersistenceAdapter implements TicketPersistence {

    private final TicketRepository ticketRepository;
    private final TicketPersistenceMapper ticketPersistenceMapper;

    public TicketPersistenceAdapter(TicketRepository ticketRepository, TicketPersistenceMapper ticketPersistenceMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketPersistenceMapper = ticketPersistenceMapper;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity entity = ticketPersistenceMapper.toEntity(ticket);
        TicketEntity savedEntity = ticketRepository.save(entity);
        return ticketPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return ticketRepository.findById(id)
                .map(entity -> ticketPersistenceMapper.toDomain(entity));
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll().stream()
                .map(ticketPersistenceMapper::toDomain)
                .toList();
    }
}