package com.example.mobileappws.controller;

import com.example.mobileappws.model.response.UserRest;
import com.example.mobileappws.service.impl.UserServiceImpl;
import com.example.mobileappws.shared.dto.AddressDto;
import com.example.mobileappws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private static final String USER_ID = "userId";
    @InjectMocks
    UserController userController;
    @Mock
    UserServiceImpl userService;

    UserDto userDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        userDto = new UserDto();
        userDto.setFirstName("firstName");
        userDto.setLastName("lastName");
        userDto.setEmail("valid@email");
        userDto.setEmailVerificationStatus(FALSE);
        userDto.setEmailVerificationToken(null);
        userDto.setUserId(USER_ID);
        userDto.setAddresses(getAddressesDto());
        userDto.setEncryptedPassword("111");
    }

    @Test
    final void getUserTest() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        UserRest user = userController.getUser(USER_ID);

        assertNotNull(user);
        assertEquals(USER_ID, user.getUserId());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getLastName(), user.getLastName());
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

}