package com.example.mobileappws.service.impl;

import com.example.mobileappws.entity.UserEntity;
import com.example.mobileappws.repository.UserRepository;
import com.example.mobileappws.security.AppProperties;
import com.example.mobileappws.security.SecurityConstants;
import com.example.mobileappws.shared.Utils;
import com.example.mobileappws.shared.dto.AddressDto;
import com.example.mobileappws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static com.example.mobileappws.shared.Utils.generateAddressId;
import static com.example.mobileappws.shared.Utils.generateUserId;
import static java.util.List.of;
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
    AppProperties appProperties;

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
        entity.setEmail("test@test.com");
        entity.setEmailVerificationToken("emailToken");
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

//    @Test
//    final void testCreateUser() {
//        AddressDto addressDto = new AddressDto();
//        addressDto.setType("shipping");
//        List<AddressDto> addressDtoList = new ArrayList<>();
//        addressDtoList.add(addressDto);
//        UserDto userDto = new UserDto();
//        userDto.setAddresses(addressDtoList);
//
//        try (MockedStatic<Utils> utilities = Mockito.mockStatic(Utils.class);
//             MockedStatic<SecurityConstants> secConst = Mockito.mockStatic(SecurityConstants.class)) {
//            when(userRepository.findByEmail(anyString())).thenReturn(entity);
//            utilities.when(() -> Utils.generateAddressId(anyInt())).thenReturn("addressID");
//            utilities.when(() -> Utils.generateUserId(anyInt())).thenReturn(userId);
//            utilities.when(() -> Utils.generateEmailVerificationToken(anyString())).thenReturn("emailToken");
//            secConst.when(SecurityConstants::getTokenSecret).thenReturn("emailToken");
//            when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
//            when(userRepository.save(any(UserEntity.class))).thenReturn(entity);
//            when(SecurityConstants.getTokenSecret()).thenReturn("emailToken");
//            when(appProperties.getTokenSecret()).thenReturn("token");
//        }
//
//        UserDto storedUser = userService.createUser(userDto);
//
//        assertNotNull(storedUser);
//        assertEquals("firstName", storedUser.getFirstName());
//        assertEquals(userId, storedUser.getUserId());
//        assertEquals("shipping", storedUser.getAddresses().get(0).getType());
//    }

}