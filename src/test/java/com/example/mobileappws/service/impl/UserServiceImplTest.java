package com.example.mobileappws.service.impl;

import com.example.mobileappws.entity.UserEntity;
import com.example.mobileappws.repository.UserRepository;
import com.example.mobileappws.shared.AmazonSES;
import com.example.mobileappws.shared.Utils;
import com.example.mobileappws.shared.dto.AddressDto;
import com.example.mobileappws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    Utils utils;
    @Mock
    AmazonSES amazonSES;

    String userId = "userId";
    String encryptedPassword = "pwd";
    UserEntity entity;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        entity = new UserEntity();
        entity.setId(1L);
        entity.setFirstName("firstName");
        entity.setUserId(userId);
        entity.setEncryptedPassword(encryptedPassword);
        entity.setEmail("valid@email.id");
        entity.setEmailVerificationToken("emailToken");
        entity.setEmailVerificationStatus(true);
    }

    @Test
    final void testGetUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(entity);

        UserDto user = userService.getUser("test@test.com");

        assertNotNull(user);
        assertEquals("firstName", user.getFirstName());
        assertEquals(userId, user.getUserId());
        assertEquals(encryptedPassword, user.getEncryptedPassword());
    }

    @Test
    final void testGetUser_UsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.getUser("test@test.com"));
    }

    @Test
    final void testCreateUser() {
        AddressDto addressDto = new AddressDto();
        addressDto.setType("shipping");
        List<AddressDto> addressDtoList = new ArrayList<>();
        addressDtoList.add(addressDto);
        UserDto userDto = new UserDto();
        userDto.setAddresses(addressDtoList);

        when(userRepository.findByEmail(anyString())).thenReturn(entity);
        when(utils.generateAddressId(anyInt())).thenReturn("addressID");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
//        when(utils.generateEmailVerificationToken(anyString())).thenReturn("emailToken");
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(ArgumentMatchers.any())).thenReturn(entity);

        UserDto storedUser = userService.createUser(userDto);

        assertNotNull(storedUser);
        assertEquals("firstName", storedUser.getFirstName());
        assertEquals(userId, storedUser.getUserId());
    }

}