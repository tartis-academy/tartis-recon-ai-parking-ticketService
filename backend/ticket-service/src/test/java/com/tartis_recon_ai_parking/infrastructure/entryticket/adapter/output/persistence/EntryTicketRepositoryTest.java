package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackageClasses = EntryTicketEntity.class)
@EnableJpaRepositories(basePackageClasses = EntryTicketRepository.class)
class EntryTicketRepositoryTest {

    @Autowired
    private EntryTicketRepository repository;

    @Test
    void guarda_y_recupera_por_id() {
        EntryTicketEntity entity = new EntryTicketEntity();
        entity.setId(UUID.randomUUID());
        entity.setStayId(UUID.randomUUID());
        entity.setCode("ABC123");
        entity.setIssuedAt(Instant.now());

        repository.save(entity);

        Optional<EntryTicketEntity> found = repository.findById(entity.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("ABC123");
    }

    @Test
    void encuentra_por_code_y_por_stayId() {
        EntryTicketEntity entity = new EntryTicketEntity();
        entity.setId(UUID.randomUUID());
        entity.setStayId(UUID.randomUUID());
        entity.setCode("XYZ999");
        entity.setIssuedAt(Instant.now());
        repository.save(entity);

        assertThat(repository.findByCode("XYZ999")).isPresent();
        assertThat(repository.findByStayId(entity.getStayId())).isPresent();
    }

    @Test
    void code_duplicado_lanza_excepcion() {
        EntryTicketEntity e1 = new EntryTicketEntity();
        e1.setId(UUID.randomUUID());
        e1.setStayId(UUID.randomUUID());
        e1.setCode("DUPLICADO");
        e1.setIssuedAt(Instant.now());
        repository.saveAndFlush(e1);

        EntryTicketEntity e2 = new EntryTicketEntity();
        e2.setId(UUID.randomUUID());
        e2.setStayId(UUID.randomUUID());
        e2.setCode("DUPLICADO");
        e2.setIssuedAt(Instant.now());

        assertThatThrownBy(() -> repository.saveAndFlush(e2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void stayId_duplicado_lanza_excepcion() {
    UUID stayId = UUID.randomUUID();

    EntryTicketEntity e1 = new EntryTicketEntity();
    e1.setId(UUID.randomUUID());
    e1.setStayId(stayId);
    e1.setCode("CODE-A");
    e1.setIssuedAt(Instant.now());
    repository.saveAndFlush(e1);

    EntryTicketEntity e2 = new EntryTicketEntity();
    e2.setId(UUID.randomUUID());
    e2.setStayId(stayId); // misma estancia
    e2.setCode("CODE-B");
    e2.setIssuedAt(Instant.now());

    assertThatThrownBy(() -> repository.saveAndFlush(e2))
            .isInstanceOf(DataIntegrityViolationException.class);
}

@Test
void no_encuentra_code_inexistente() {
    assertThat(repository.findByCode("NO-EXISTE")).isEmpty();
}

@Test
void no_encuentra_stayId_inexistente() {
    assertThat(repository.findByStayId(UUID.randomUUID())).isEmpty();
}
}