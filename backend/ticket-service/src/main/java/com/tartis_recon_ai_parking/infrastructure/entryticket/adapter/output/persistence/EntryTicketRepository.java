package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntryTicketRepository extends JpaRepository<EntryTicketEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EntryTicketEntity> findById(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EntryTicketEntity> findByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EntryTicketEntity> findByStayId(UUID stayId);

    List<EntryTicketEntity> findAll();
}
