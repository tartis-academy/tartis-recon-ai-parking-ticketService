package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence;

import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import org.springframework.stereotype.Component;

@Component
public class EntryTicketPersistenceMapper {

    public EntryTicketEntity toEntity(EntryTicket entryTicket) {
        EntryTicketEntity entity = new EntryTicketEntity();
        entity.setId(entryTicket.getId());
        entity.setStayId(entryTicket.getStayId());
        entity.setCode(entryTicket.getCode());
        entity.setIssuedAt(entryTicket.getIssuedAt());
        entity.setUsedAt(entryTicket.getUsedAt());
        return entity;
    }

    public EntryTicket toDomain(EntryTicketEntity entity) {
        return EntryTicket.restore(
                entity.getId(),
                entity.getStayId(),
                entity.getCode(),
                entity.getIssuedAt(),
                entity.getUsedAt()
        );
    }
}