package io.github.diegohahn.ado.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import io.github.diegohahn.ado.model.UserInformation;
import io.github.diegohahn.ado.repository.UserInformationRepository;

/**
 * Runs against the in-memory H2 database configured in src/test/resources.
 */
@SpringBootTest
class TokenEncryptionIntegrationTest {

    @Autowired
    private UserInformationRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LegacyTokenEncryptionMigration migration;

    @BeforeEach
    void cleanTables() {
        jdbcTemplate.update("DELETE FROM activity_records");
        jdbcTemplate.update("DELETE FROM user_information");
    }

    private String rawToken(Long userId) {
        return jdbcTemplate.queryForObject(
                "SELECT token FROM user_information WHERE user_id = ?", String.class, userId);
    }

    /** Simulates a row written before encryption existed. */
    private void insertLegacyRow(String email, String plaintextToken) {
        UserInformation user = repository.save(new UserInformation(null, email, "sk-" + email, "board", null));
        jdbcTemplate.update("UPDATE user_information SET token = ? WHERE user_id = ?", plaintextToken, user.getUserId());
        assertEquals(plaintextToken, rawToken(user.getUserId()));
    }

    @Test
    void savedTokenIsEncryptedInTheDatabaseAndDecryptedOnRead() {
        UserInformation user = repository.save(new UserInformation(null, "a@example.com", "sk-a", "board", "plain-pat"));

        String stored = rawToken(user.getUserId());
        assertTrue(stored.startsWith(TokenCipher.PREFIX));
        assertEquals("plain-pat", repository.findByUserId(user.getUserId()).getToken());
    }

    @Test
    void legacyPlaintextRowIsReadAndEncryptedOnNextSave() {
        insertLegacyRow("legacy@example.com", "legacy-pat");

        UserInformation legacy = repository.findByEmail("legacy@example.com");
        assertEquals("legacy-pat", legacy.getToken());

        legacy.setBoard("other-board");
        repository.save(legacy);

        assertTrue(rawToken(legacy.getUserId()).startsWith(TokenCipher.PREFIX));
        assertEquals("legacy-pat", repository.findByEmail("legacy@example.com").getToken());
    }

    @Test
    void startupMigrationEncryptsLegacyRows() {
        insertLegacyRow("old@example.com", "old-pat");

        assertEquals(1, migration.encryptLegacyTokens());
        assertEquals(0, migration.encryptLegacyTokens());

        UserInformation user = repository.findByEmail("old@example.com");
        assertTrue(rawToken(user.getUserId()).startsWith(TokenCipher.PREFIX));
        assertEquals("old-pat", user.getToken());
    }
}
