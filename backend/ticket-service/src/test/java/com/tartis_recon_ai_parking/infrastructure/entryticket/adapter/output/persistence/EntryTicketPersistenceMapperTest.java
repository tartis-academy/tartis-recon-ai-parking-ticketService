package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence;

import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntryTicketPersistenceMapperTest {

    private final EntryTicketPersistenceMapper mapper = new EntryTicketPersistenceMapper();

    @Test
    void toEntity_mapea_todos_los_campos() {
        EntryTicket ticket = EntryTicket.recreate(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), "ABC123");
        // usamos recreate para tener control sobre los valores en el test

        EntryTicketEntity entity = mapper.toEntity(ticket);

        assertThat(entity.getId()).isEqualTo(ticket.getUniqueId());
        assertThat(entity.getStayId()).isEqualTo(ticket.getStayId());
        assertThat(entity.getCode()).isEqualTo(ticket.getCode());
        assertThat(entity.getIssuedAt()).isEqualTo(ticket.getIssuedAt());
    }

    @Test
    void toDomain_mapea_todos_los_campos() {
        EntryTicketEntity entity = new EntryTicketEntity();
        entity.setId(UUID.randomUUID());
        entity.setStayId(UUID.randomUUID());
        entity.setCode("XYZ999");
        entity.setIssuedAt(Instant.now());

        EntryTicket ticket = mapper.toDomain(entity);

        assertThat(ticket.getUniqueId()).isEqualTo(entity.getId());
        assertThat(ticket.getStayId()).isEqualTo(entity.getStayId());
        assertThat(ticket.getCode()).isEqualTo(entity.getCode());
        assertThat(ticket.getIssuedAt()).isEqualTo(entity.getIssuedAt());
    }
}