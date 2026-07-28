package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence;

import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
@EntityScan(basePackageClasses = EntryTicketEntity.class)
@EnableJpaRepositories(basePackageClasses = EntryTicketRepository.class)
@Import({EntryTicketPersistenceMapper.class, EntryTicketPersistenceAdapter.class})
class EntryTicketPersistenceAdapterTest {

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