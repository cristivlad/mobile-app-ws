package com.example.mobileappws.service.impl;

import com.example.mobileappws.entity.UserEntity;
import com.example.mobileappws.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    }

}