package io.github.diegohahn.ado.security;

import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter that keeps Personal Access Tokens encrypted in the database.
 * Spring Boot registers Spring as Hibernate's bean container, so Hibernate gets
 * this converter from Spring with the {@link TokenCipher} already injected.
 */
@Component
@Converter
public class EncryptedTokenConverter implements AttributeConverter<String, String> {

    private final TokenCipher tokenCipher;

    public EncryptedTokenConverter(TokenCipher tokenCipher) {
        this.tokenCipher = tokenCipher;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return tokenCipher.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return tokenCipher.decrypt(dbData);
    }
}
