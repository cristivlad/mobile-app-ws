package com.example.mobileappws.controller;

import com.example.mobileappws.exceptions.UserServiceException;
import com.example.mobileappws.model.request.UserDetailsRequestModel;
import com.example.mobileappws.model.response.UserRest;
import com.example.mobileappws.service.impl.UserServiceImpl;
import com.example.mobileappws.shared.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import static com.example.mobileappws.model.response.ErrorMessage.MISSING_REQUIRED_FIELD;
import static org.springframework.beans.BeanUtils.copyProperties;

@RestController
@RequestMapping("/users")
public class UserController {

    UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserRest getUser(@PathVariable(value = "userId") String userId) {
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(userId);
        copyProperties(userDto, returnValue);

        return returnValue;
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException {

        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(MISSING_REQUIRED_FIELD.getError());

        UserDto userDto = new UserDto();
        copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        copyProperties(createdUser, returnValue);

        return returnValue;
    }

    @PutMapping("/{userId}")
    public UserRest updateUser(@PathVariable(value = "userId") String userId, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(MISSING_REQUIRED_FIELD.getError());

        UserDto userDto = new UserDto();
        copyProperties(userDetails,userDto);

        UserDto updatedUser = userService.updateUser(userId, userDto);
        copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @DeleteMapping
    public String deleteUser() {
        return "delete user was called";
    }
}
