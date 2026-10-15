package dse.thesis404.testutil;

import dse.thesis404.infrastructure.persistence.jdbc.DateiSynchronisierung;
import dse.thesis404.infrastructure.persistence.jdbc.JdbcBetreuerProfilRepository;
import dse.thesis404.infrastructure.persistence.jdbc.JdbcThemeRepository;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Slice-Test für die Persistenz-Schicht:
 * - @DataJdbcTest lädt nur Datenbank, Flyway und Spring-Data-JDBC-Repositories (kein Web, keine Security, keine Services)
 *   → startet deutlich schneller als @SpringBootTest.
 * - Jeder Test läuft in einer Transaktion, die danach zurückgerollt wird → Tests beeinflussen sich nicht.
 * - replace = NONE: die echte PostgreSQL aus Testcontainers benutzen, keine eingebettete Ersatz-DB.
 * - @Import: unsere Adapter (Ports → Spring Data JDBC), die nicht automatisch zum Slice gehören.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({JdbcThemeRepository.class, JdbcBetreuerProfilRepository.class, DateiSynchronisierung.class})
public @interface PersistenzTest {
}
