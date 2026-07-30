package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence;

import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class EntryTicketPersistenceAdapter implements EntryTicketPersistence {

    private final EntryTicketRepository repository;
    private final EntryTicketPersistenceMapper mapper;

    public EntryTicketPersistenceAdapter(EntryTicketRepository repository,
                                          EntryTicketPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public EntryTicket save(EntryTicket entryTicket) {
        EntryTicketEntity entity = mapper.toEntity(entryTicket);
        EntryTicketEntity saved = repository.saveAndFlush(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<EntryTicket> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<EntryTicket> findByCode(String code) {
        return repository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public Optional<EntryTicket> findByStayId(UUID stayId) {
        return repository.findByStayId(stayId).map(mapper::toDomain);
    }

    @Override
    public List<EntryTicket> findAll() {
        // TODO Auto-generated method stub
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<EntryTicket> findByIdForUpdate(UUID id) {
        return repository.findByIdForUpdate(id).map(mapper::toDomain);
    }
}