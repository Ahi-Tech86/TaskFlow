package com.ahicode.TextMe.unit.config.jwt;

import com.ahicode.TextMe.config.security.jwt.JwtKeyProvider;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtKeyProviderTest {

    private JwtKeyProvider jwtKeyProvider;

    @BeforeEach
    void setup() {
        jwtKeyProvider = new JwtKeyProvider();
    }

    @Test
    void shouldGetSignKeyWithValidSecretKey() {
        String secretKey = "dGVzdFNlY3JldEtleVRlc3RTZWNyZXRLZXlUZXN0ZXN0ZXN0ZXN0ZXN0ZXN0ZXN";

        Key resultKey = jwtKeyProvider.getSignKey(secretKey);

        assertNotNull(resultKey);
        assertEquals("HmacSHA256", resultKey.getAlgorithm());
    }

    @Test
    void shouldThrowErrorWithInvalidSecretKey() {
        String invalidSecretKey = "invalidSecretKey";

        try {
            jwtKeyProvider.getSignKey(invalidSecretKey);
        } catch (WeakKeyException e) {
            assertEquals(
                    "The specified key byte array is 96 bits which is not secure enough for any JWT HMAC-SHA " +
                            "algorithm.  The JWT JWA Specification (RFC 7518, Section 3.2) states that keys used with " +
                            "HMAC-SHA algorithms MUST have a size >= 256 bits (the key size must be greater than or " +
                            "equal to the hash output size).  Consider using the io.jsonwebtoken.security.Keys#secret" +
                            "KeyFor(SignatureAlgorithm) method to create a key guaranteed to be secure enough for your" +
                            " preferred HMAC-SHA algorithm.  See https://tools.ietf.org/html/rfc7518#section-3.2 for " +
                            "more information.",
                    e.getMessage()
            );
        }
    }
}
