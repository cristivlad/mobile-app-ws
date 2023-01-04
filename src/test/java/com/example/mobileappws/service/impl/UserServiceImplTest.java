package com.example.mobileappws.service.impl;

import com.example.mobileappws.entity.UserEntity;
import com.example.mobileappws.repository.UserRepository;
import com.example.mobileappws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    final void testGetUser() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setFirstName("firstName");
        entity.setUserId("userId");
        entity.setEncryptedPassword("pwd");

        when(userRepository.findByEmail(anyString())).thenReturn(entity);

        UserDto user = userService.getUser("test@test.com");

        assertNotNull(user);
        assertEquals("firstName", user.getFirstName());
        assertEquals("userId", user.getUserId());
        assertEquals("pwd", user.getEncryptedPassword());
    }

    @Test
    final void testGetUser_UsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.getUser("test@test.com"));
    }

}