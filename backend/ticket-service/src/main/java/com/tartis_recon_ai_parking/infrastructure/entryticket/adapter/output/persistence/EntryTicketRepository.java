package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface EntryTicketRepository extends JpaRepository<EntryTicketEntity, UUID> {
    Optional<EntryTicketEntity> findByCode(String code);
    Optional<EntryTicketEntity> findByStayId(UUID stayId);
}
