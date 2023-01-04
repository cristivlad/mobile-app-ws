package com.example.mobileappws.service.impl;

import com.example.mobileappws.entity.AddressEntity;
import com.example.mobileappws.entity.UserEntity;
import com.example.mobileappws.repository.UserRepository;
import com.example.mobileappws.shared.Utils;
import com.example.mobileappws.shared.dto.AddressDto;
import com.example.mobileappws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    Utils utils;

    String userId = "userId";
    String encryptedPassword = "pwd";
    UserEntity entity;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        entity = new UserEntity();
        entity.setId(1L);
        entity.setFirstName("firstName");
        entity.setLastName("lastName");
        entity.setUserId(userId);
        entity.setEncryptedPassword(encryptedPassword);
        entity.setEmail("valid1@email.id");
        entity.setEmailVerificationToken("emailToken");
        entity.setEmailVerificationStatus(true);
        entity.setAddresses(getAddressesEntities());
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

    @Disabled("Not working")
    @Test
    final void testCreateUser() {
        AddressDto addressDto = new AddressDto();
        addressDto.setType("shipping");
        addressDto.setCity("Bucharest");
        addressDto.setCountry("Romania");
        addressDto.setPostalCode("ABC111");
        addressDto.setStreetName("Straduta");
        AddressDto billingAddressDto = new AddressDto();
        billingAddressDto.setType("billing");
        billingAddressDto.setCity("Bucharest");
        billingAddressDto.setCountry("Romania");
        billingAddressDto.setPostalCode("ABC111");
        billingAddressDto.setStreetName("Straduta");

        List<AddressDto> addressDtoList = new ArrayList<>();
        addressDtoList.add(addressDto);
        addressDtoList.add(billingAddressDto);
        UserDto userDto = new UserDto();
        userDto.setAddresses(addressDtoList);
        userDto.setFirstName("firstName");
        userDto.setLastName("lastName");
        userDto.setPassword("1234");
        userDto.setEmail("valid@email1.com");

        when(userRepository.findByEmail(anyString())).thenReturn(entity);
        when(utils.generateAddressId(anyInt())).thenReturn("addressID");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(entity);

        UserDto storedUser = userService.createUser(userDto);

        assertNotNull(storedUser);
        assertEquals(entity.getFirstName(), storedUser.getFirstName());
        assertEquals(userId, storedUser.getUserId());
        assertNotNull(storedUser.getUserId());
        assertEquals(storedUser.getAddresses().size(), entity.getAddresses().size());
        verify(utils, times(2)).generateAddressId(anyInt());
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    private List<AddressDto> getAddressesDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setType("shipping");
        addressDto.setCity("Bucharest");
        addressDto.setCountry("Romania");
        addressDto.setPostalCode("ABC111");
        addressDto.setStreetName("Straduta");
        AddressDto billingAddressDto = new AddressDto();
        billingAddressDto.setType("billing");
        billingAddressDto.setCity("Bucharest");
        billingAddressDto.setCountry("Romania");
        billingAddressDto.setPostalCode("ABC111");
        billingAddressDto.setStreetName("Straduta");

        return List.of(addressDto, billingAddressDto);
    }

    private List<AddressEntity> getAddressesEntities() {
        List<AddressDto> addresses = getAddressesDto();
        var listType = new TypeToken<List<AddressEntity>>() {}.getType();

        return new ModelMapper().map(addresses, listType);
    }

}