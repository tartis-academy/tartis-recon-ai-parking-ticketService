package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@Testcontainers
@EntityScan(basePackageClasses = TicketEntity.class)
@EnableJpaRepositories(basePackageClasses = TicketRepository.class)
@Import({TicketPersistenceMapperImpl.class, TicketPersistenceAdapter.class})
class TicketPersistenceAdapterIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TicketPersistence adapter;

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
    void testConcurrencyPessimisticLockingGoodPath() throws Exception {
        UUID stayId = UUID.randomUUID();
        Ticket ticket = Ticket.create(stayId, Instant.now(), BigDecimal.TEN);
        transactionTemplate.execute(status -> adapter.save(ticket));

        UUID ticketId = ticket.getUniqueId();
        CountDownLatch lockAcquiredLatch = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Thread A: locks the row and holds it
        Future<Void> threadA = executor.submit(() -> {
            transactionTemplate.execute(status -> {
                Optional<Ticket> found = adapter.findById(ticketId);
                assertThat(found).isPresent();
                lockAcquiredLatch.countDown();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return null;
            });
            return null;
        });

        // Thread B: tries to lock the row, blocks, and resumes after Thread A commits
        Future<Long> threadB = executor.submit(() -> {
            lockAcquiredLatch.await();
            long startTime = System.currentTimeMillis();
            return transactionTemplate.execute(status -> {
                Optional<Ticket> found = adapter.findById(ticketId);
                long duration = System.currentTimeMillis() - startTime;
                assertThat(found).isPresent();
                return duration;
            });
        });

        threadA.get();
        long blockedDuration = threadB.get();
        executor.shutdown();

        assertThat(blockedDuration).isGreaterThanOrEqualTo(800);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void testConcurrencyPessimisticLockingBadPath() throws Exception {
        UUID stayId = UUID.randomUUID();
        Ticket ticket = Ticket.create(stayId, Instant.now(), BigDecimal.TEN);
        transactionTemplate.execute(status -> adapter.save(ticket));

        UUID ticketId = ticket.getUniqueId();
        CountDownLatch lockAcquiredLatch = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Thread A: locks the row and holds it
        Future<Void> threadA = executor.submit(() -> {
            transactionTemplate.execute(status -> {
                Optional<Ticket> found = adapter.findById(ticketId);
                assertThat(found).isPresent();
                lockAcquiredLatch.countDown();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return null;
            });
            return null;
        });

        // Thread B: tries to lock the row with a 200ms timeout, should fail
        Future<Exception> threadB = executor.submit(() -> {
            lockAcquiredLatch.await();
            try {
                transactionTemplate.execute(status -> {
                    entityManager.createNativeQuery("SET LOCAL lock_timeout = '200ms'").executeUpdate();
                    adapter.findById(ticketId);
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
