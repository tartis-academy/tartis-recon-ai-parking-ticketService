package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntryTicketRepository extends JpaRepository<EntryTicketEntity, UUID> {

    Optional<EntryTicketEntity> findByCode(String code);

    Optional<EntryTicketEntity> findByStayId(UUID stayId);

    List<EntryTicketEntity> findAll();

    /**
     * Lectura con bloqueo pesimista — usar únicamente en caminos que mutan la fila
     * (e.g. {@link com.tartis_recon_ai_parking.application.entryticket.usecase.UpdateEntryTicketUseCase}).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EntryTicketEntity e where e.id = :id")
    Optional<EntryTicketEntity> findByIdForUpdate(@Param("id") UUID id);
}
