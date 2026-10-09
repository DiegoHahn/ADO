package io.github.diegohahn.ado.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;

import org.junit.jupiter.api.Test;

class EncryptedTokenConverterTest {

    private final EncryptedTokenConverter converter =
            new EncryptedTokenConverter(new TokenCipher(Base64.getEncoder().encodeToString(new byte[32])));

    @Test
    void writesEncryptedValueAndReadsItBack() {
        String column = converter.convertToDatabaseColumn("my-token");

        assertTrue(column.startsWith(TokenCipher.PREFIX));
        assertEquals("my-token", converter.convertToEntityAttribute(column));
    }

    @Test
    void readsLegacyPlaintextColumn() {
        assertEquals("legacy-token", converter.convertToEntityAttribute("legacy-token"));
    }

    @Test
    void keepsNulls() {
        assertNull(converter.convertToDatabaseColumn(null));
        assertNull(converter.convertToEntityAttribute(null));
    }
}
