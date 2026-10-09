package io.github.diegohahn.ado.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Encrypts and decrypts the Azure DevOps Personal Access Tokens stored in the
 * database with AES-256-GCM.
 *
 * <p>Stored format: {@code enc:v1:<base64(iv || ciphertext || tag)>}, with a
 * random 12-byte IV per value. Values without the prefix are treated as legacy
 * plaintext so rows written before encryption existed can still be read.
 */
@Component
public class TokenCipher {

    public static final String PREFIX = "enc:v1:";

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_LENGTH_BYTES = 32;
    private static final int IV_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();

    public TokenCipher(@Value("${ado.security.pat-encryption-key:}") String base64Key) {
        this.key = parseKey(base64Key);
    }

    public String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] payload = ByteBuffer.allocate(iv.length + ciphertext.length).put(iv).put(ciphertext).array();
            return PREFIX + Base64.getEncoder().encodeToString(payload);
        } catch (GeneralSecurityException e) {
            throw new TokenEncryptionException("Could not encrypt the token", e);
        }
    }

    public String decrypt(String stored) {
        if (stored == null || !isEncrypted(stored)) {
            // Legacy plaintext value: it is encrypted again the next time it is written.
            return stored;
        }
        byte[] payload;
        try {
            payload = Base64.getDecoder().decode(stored.substring(PREFIX.length()));
        } catch (IllegalArgumentException e) {
            throw new TokenEncryptionException("Stored token is not valid base64", e);
        }
        if (payload.length <= IV_LENGTH_BYTES) {
            throw new TokenEncryptionException("Stored token is too short to be valid");
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, payload, 0, IV_LENGTH_BYTES));
            byte[] plaintext = cipher.doFinal(payload, IV_LENGTH_BYTES, payload.length - IV_LENGTH_BYTES);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            // Wrong key, corrupted value or tampering. Never fall back to the raw value.
            throw new TokenEncryptionException("Could not decrypt the stored token", e);
        }
    }

    public static boolean isEncrypted(String value) {
        return value != null && value.startsWith(PREFIX);
    }

    private static SecretKey parseKey(String base64Key) {
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalStateException(
                    "PAT_ENCRYPTION_KEY is not set. The API needs it to encrypt the Azure DevOps tokens it stores. "
                            + "Generate one with 'openssl rand -base64 32' and set it before starting the application.");
        }
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(base64Key.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("PAT_ENCRYPTION_KEY must be valid base64.", e);
        }
        if (keyBytes.length != KEY_LENGTH_BYTES) {
            throw new IllegalStateException("PAT_ENCRYPTION_KEY must decode to " + KEY_LENGTH_BYTES
                    + " bytes (AES-256), but it decodes to " + keyBytes.length + ".");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }
}
