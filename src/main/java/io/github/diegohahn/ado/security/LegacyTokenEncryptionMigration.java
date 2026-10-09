package io.github.diegohahn.ado.security;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Encrypts tokens that were stored in plaintext before encryption existed.
 * It runs on every startup and does nothing once all rows are encrypted.
 * Each UPDATE only matches if the row still holds the same plaintext value,
 * so a concurrent write is never overwritten.
 */
@Component
public class LegacyTokenEncryptionMigration implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(LegacyTokenEncryptionMigration.class);

    private final JdbcTemplate jdbcTemplate;
    private final TokenCipher tokenCipher;

    public LegacyTokenEncryptionMigration(JdbcTemplate jdbcTemplate, TokenCipher tokenCipher) {
        this.jdbcTemplate = jdbcTemplate;
        this.tokenCipher = tokenCipher;
    }

    @Override
    public void run(ApplicationArguments args) {
        encryptLegacyTokens();
    }

    int encryptLegacyTokens() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT user_id, token FROM user_information WHERE token IS NOT NULL AND token NOT LIKE ?",
                TokenCipher.PREFIX + "%");
        int updated = 0;
        for (Map<String, Object> row : rows) {
            Object value = row.get("token");
            if (!(value instanceof String token) || token.isEmpty() || TokenCipher.isEncrypted(token)) {
                continue;
            }
            updated += jdbcTemplate.update(
                    "UPDATE user_information SET token = ? WHERE user_id = ? AND token = ?",
                    tokenCipher.encrypt(token), row.get("user_id"), token);
        }
        if (updated > 0) {
            logger.info("Encrypted {} Azure DevOps token(s) that were stored in plaintext", updated);
        }
        return updated;
    }
}
