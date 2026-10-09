package io.github.diegohahn.ado.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import java.util.Base64;

import org.junit.jupiter.api.Test;

class TokenCipherTest {

    private static String randomKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    private final TokenCipher cipher = new TokenCipher(randomKey());

    @Test
    void encryptThenDecrypt_returnsOriginalValue() {
        String token = "a-personal-access-token-with-ümlauts";

        String stored = cipher.encrypt(token);

        assertTrue(stored.startsWith(TokenCipher.PREFIX));
        assertNotEquals(token, stored);
        assertEquals(token, cipher.decrypt(stored));
    }

    @Test
    void encrypt_usesRandomIvPerValue() {
        String token = "same-token";

        assertNotEquals(cipher.encrypt(token), cipher.encrypt(token));
    }

    @Test
    void nullValues_passThrough() {
        assertNull(cipher.encrypt(null));
        assertNull(cipher.decrypt(null));
    }

    @Test
    void decrypt_legacyPlaintext_returnsValueUnchanged() {
        assertEquals("legacy-plaintext-token", cipher.decrypt("legacy-plaintext-token"));
    }

    @Test
    void decrypt_tamperedCiphertext_throws() {
        String stored = cipher.encrypt("token");
        byte[] payload = Base64.getDecoder().decode(stored.substring(TokenCipher.PREFIX.length()));
        payload[payload.length - 1] ^= 0x01;
        String tampered = TokenCipher.PREFIX + Base64.getEncoder().encodeToString(payload);

        assertThrows(TokenEncryptionException.class, () -> cipher.decrypt(tampered));
    }

    @Test
    void decrypt_withDifferentKey_throws() {
        String stored = cipher.encrypt("token");
        TokenCipher otherCipher = new TokenCipher(randomKey());

        assertThrows(TokenEncryptionException.class, () -> otherCipher.decrypt(stored));
    }

    @Test
    void decrypt_malformedPayload_throws() {
        assertThrows(TokenEncryptionException.class, () -> cipher.decrypt(TokenCipher.PREFIX + "not base64!"));
        assertThrows(TokenEncryptionException.class, () -> cipher.decrypt(TokenCipher.PREFIX + "AAAA"));
    }

    @Test
    void constructor_missingKey_failsWithClearMessage() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> new TokenCipher(""));
        assertTrue(e.getMessage().contains("PAT_ENCRYPTION_KEY"));
        assertThrows(IllegalStateException.class, () -> new TokenCipher(null));
    }

    @Test
    void constructor_wrongKeyLength_fails() {
        String shortKey = Base64.getEncoder().encodeToString(new byte[16]);
        assertThrows(IllegalStateException.class, () -> new TokenCipher(shortKey));
    }

    @Test
    void constructor_invalidBase64_fails() {
        assertThrows(IllegalStateException.class, () -> new TokenCipher("not base64!"));
    }
}
