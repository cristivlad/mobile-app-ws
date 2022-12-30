package com.example.mobileappws.controller;

import com.example.mobileappws.exceptions.UserServiceException;
import com.example.mobileappws.model.request.UserDetailsRequestModel;
import com.example.mobileappws.model.response.*;
import com.example.mobileappws.service.impl.AddressServiceImpl;
import com.example.mobileappws.service.impl.UserServiceImpl;
import com.example.mobileappws.shared.dto.AddressDto;
import com.example.mobileappws.shared.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.mobileappws.model.response.ErrorMessage.MISSING_REQUIRED_FIELD;
import static java.util.List.of;
import static org.springframework.beans.BeanUtils.copyProperties;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private static final String ADDRESSES = "addresses";
    private final UserServiceImpl userService;
    private final AddressServiceImpl addressService;

    @GetMapping("/{userId}")
    public UserRest getUser(@PathVariable(value = "userId") String userId) {
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(userId);
        copyProperties(userDto, returnValue);

        return returnValue;
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException {

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(MISSING_REQUIRED_FIELD.getError());

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        return modelMapper.map(createdUser, UserRest.class);
    }

    @PutMapping("/{userId}")
    public UserRest updateUser(@PathVariable(value = "userId") String userId, @RequestBody UserDetailsRequestModel userDetails) {
        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(MISSING_REQUIRED_FIELD.getError());

        UserDto userDto = new UserDto();
        copyProperties(userDetails,userDto);

        UserDto updatedUser = userService.updateUser(userId, userDto);
        UserRest returnValue = new UserRest();
        copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping("/{userId}")
    public OperationStatusModel deleteUser(@PathVariable(value = "userId") String userId) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(userId);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @GetMapping
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "25") int limit) {
        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto userDto : users) {
            UserRest userModel = new UserRest();
            copyProperties(userDto, userModel);
            returnValue.add(userModel);
        }
        return returnValue;
    }

    @GetMapping("/{userId}/addresses")
    public CollectionModel<AddressesRest> getUserAddresses(@PathVariable String userId) {
        var returnValue = new ArrayList<AddressesRest>();

        List<AddressDto> addressDtos = addressService.getAddresses(userId);
        if (addressDtos != null && !addressDtos.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
            returnValue = new ModelMapper().map(addressDtos, listType);
        }
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId)).withSelfRel();

        return CollectionModel.of(returnValue, userLink, selfLink);
    }

    @GetMapping("/{userId}/addresses/{addressId}")
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
        var addressDto = addressService.getAddress(addressId);
        var modelMapper = new ModelMapper();
        var returnValue = modelMapper.map(addressDto, AddressesRest.class);

        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
        Link userAddressesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId)).withRel(ADDRESSES);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();

        return EntityModel.of(returnValue, of(userLink, userAddressesLink, selfLink));
    }

}
