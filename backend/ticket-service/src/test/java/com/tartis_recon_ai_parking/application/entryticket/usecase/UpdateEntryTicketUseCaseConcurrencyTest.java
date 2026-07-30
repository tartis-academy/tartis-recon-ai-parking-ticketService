package com.tartis_recon_ai_parking.application.entryticket.usecase;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence.EntryTicketEntity;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence.EntryTicketPersistenceAdapter;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence.EntryTicketPersistenceMapper;
import com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence.EntryTicketRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@DataJpaTest(properties = {
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@Testcontainers
@EntityScan(basePackageClasses = EntryTicketEntity.class)
@EnableJpaRepositories(basePackageClasses = EntryTicketRepository.class)
@Import({
        EntryTicketPersistenceMapper.class,
        EntryTicketPersistenceAdapter.class,
        UpdateEntryTicketUseCase.class
})
class UpdateEntryTicketUseCaseConcurrencyTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private UpdateEntryTicketUseCase updateEntryTicketUseCase;

    @MockitoSpyBean
    private EntryTicketPersistence adapter;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManager entityManager;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUpTransactionTemplate() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testConcurrencyPessimisticLockingOnUseCaseGoodPath() throws Exception {
        UUID oldStayId = UUID.randomUUID();
        EntryTicket ticket = EntryTicket.recreate(UUID.randomUUID(), oldStayId, Instant.now(), "UC-CONCURRENCY-GOOD");
        
        transactionTemplate.execute(status -> {
            EntryTicketEntity entity = new EntryTicketEntity();
            entity.setId(ticket.getUniqueId());
            entity.setStayId(ticket.getStayId());
            entity.setIssuedAt(ticket.getIssuedAt());
            entity.setCode(ticket.getCode());
            entityManager.persist(entity);
            return null;
        });

        UUID ticketId = ticket.getUniqueId();
        CountDownLatch lockAcquiredLatch = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Spy adapter save to block inside transaction A
        doAnswer(invocation -> {
            // Count down so thread B starts trying to acquire lock
            lockAcquiredLatch.countDown();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return invocation.callRealMethod();
        }).when(adapter).save(any());

        // Thread A: calls usecase which locks and then sleeps during save (inside transaction)
        UUID newStayIdA = UUID.randomUUID();
        Future<Void> threadA = executor.submit(() -> {
            updateEntryTicketUseCase.execute(ticketId, new EntryTicketCreateDTO(newStayIdA));
            return null;
        });

        // Thread B: tries to call usecase, must block until thread A commits
        UUID newStayIdB = UUID.randomUUID();
        Future<Long> threadB = executor.submit(() -> {
            lockAcquiredLatch.await();
            long startTime = System.currentTimeMillis();
            updateEntryTicketUseCase.execute(ticketId, new EntryTicketCreateDTO(newStayIdB));
            return System.currentTimeMillis() - startTime;
        });

        threadA.get();
        long blockedDuration = threadB.get();
        executor.shutdown();

        // Verify that thread B had to wait at least 800ms for thread A to finish and commit
        assertThat(blockedDuration).isGreaterThanOrEqualTo(800);

        // Verify the final stayId is from Thread B (the second one to commit)
        Optional<EntryTicket> finalTicket = adapter.findById(ticketId);
        assertThat(finalTicket).isPresent();
        assertThat(finalTicket.get().getStayId()).isEqualTo(newStayIdB);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testConcurrencyPessimisticLockingOnUseCaseBadPath() throws Exception {
        UUID oldStayId = UUID.randomUUID();
        EntryTicket ticket = EntryTicket.recreate(UUID.randomUUID(), oldStayId, Instant.now(), "UC-CONCURRENCY-BAD");
        transactionTemplate.execute(status -> {
            EntryTicketEntity entity = new EntryTicketEntity();
            entity.setId(ticket.getUniqueId());
            entity.setStayId(ticket.getStayId());
            entity.setIssuedAt(ticket.getIssuedAt());
            entity.setCode(ticket.getCode());
            entityManager.persist(entity);
            return null;
        });

        UUID ticketId = ticket.getUniqueId();
        CountDownLatch lockAcquiredLatch = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Spy adapter save to block inside transaction A
        doAnswer(invocation -> {
            lockAcquiredLatch.countDown();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return invocation.callRealMethod();
        }).when(adapter).save(any());

        // Thread A: calls usecase which locks and then sleeps during save (inside transaction)
        UUID newStayIdA = UUID.randomUUID();
        Future<Void> threadA = executor.submit(() -> {
            updateEntryTicketUseCase.execute(ticketId, new EntryTicketCreateDTO(newStayIdA));
            return null;
        });

        // Thread B: tries to call usecase with a 200ms lock timeout, should fail
        UUID newStayIdB = UUID.randomUUID();
        Future<Exception> threadB = executor.submit(() -> {
            lockAcquiredLatch.await();
            try {
                transactionTemplate.execute(status -> {
                    // Set lock timeout to 200ms
                    entityManager.createNativeQuery("SET LOCAL lock_timeout = '200ms'").executeUpdate();
                    updateEntryTicketUseCase.execute(ticketId, new EntryTicketCreateDTO(newStayIdB));
                    return null;
                });
                return null;
            } catch (Exception e) {
                return e;
            }
        });

        Exception ex = threadB.get();
        threadA.get();
        executor.shutdown();

        assertThat(ex).isNotNull();
        assertThat(ex)
            .isInstanceOfAny(
                org.springframework.dao.PessimisticLockingFailureException.class,
                org.springframework.dao.CannotAcquireLockException.class
            );
    }
}
