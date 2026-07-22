package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
     Optional<TicketEntity> findById(UUID plate);
}
