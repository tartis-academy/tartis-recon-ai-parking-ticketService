package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.UUID;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TicketEntity> findById(UUID id);
}
