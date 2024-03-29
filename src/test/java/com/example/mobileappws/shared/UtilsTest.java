package com.example.mobileappws.shared;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UtilsTest {
    @Autowired
    Utils utils;

    @BeforeEach
    void setUp() {

    }

    @Test
    final void testGenerateUserId() {
        String userId = utils.generateUserId(30);
        String userId2 = utils.generateUserId(30);

        assertNotNull(userId);
        assertNotNull(userId2);
        assertEquals(30, userId.length());
        assertFalse(userId.equalsIgnoreCase(userId2));
    }

    @Test
    final void testHasTokenNotExpired() {
        String token = utils.generateEmailVerificationToken("userId");
        assertNotNull(token);

        boolean hasTokenExpired = utils.hasTokenExpired(token);

        assertFalse(hasTokenExpired);
    }
}