package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence;

import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@Testcontainers
class EntryTicketPersistenceAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private EntryTicketPersistence adapter;

    @Test
    void guarda_y_recupera_por_id_a_traves_del_puerto() {
        EntryTicket ticket = EntryTicket.recreate(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), "ADAPTER-1");

        EntryTicket saved = adapter.save(ticket);

        Optional<EntryTicket> found = adapter.findById(saved.getUniqueId());
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("ADAPTER-1");
    }

    @Test
    void encuentra_por_code_a_traves_del_puerto() {
        EntryTicket ticket = EntryTicket.recreate(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), "ADAPTER-2");
        adapter.save(ticket);

        Optional<EntryTicket> found = adapter.findByCode("ADAPTER-2");
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("ADAPTER-2");
    }

    @Test
    void encuentra_por_stayId_a_traves_del_puerto() {
        UUID stayId = UUID.randomUUID();
        EntryTicket ticket = EntryTicket.recreate(UUID.randomUUID(), stayId, Instant.now(), "ADAPTER-3");
        adapter.save(ticket);

        Optional<EntryTicket> found = adapter.findByStayId(stayId);
        assertThat(found).isPresent();
        assertThat(found.get().getStayId()).isEqualTo(stayId);
    }

    @Test
    void findById_inexistente_devuelve_vacio() {
        Optional<EntryTicket> found = adapter.findById(UUID.randomUUID());
        assertThat(found).isEmpty();
    }

    @Test
    void findByCode_inexistente_devuelve_vacio() {
        Optional<EntryTicket> found = adapter.findByCode("NO-EXISTE");
        assertThat(found).isEmpty();
    }
}