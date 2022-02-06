package com.example.mobileappws.controller;

import com.example.mobileappws.model.request.UserDetailsRequestModel;
import com.example.mobileappws.model.response.UserRest;
import com.example.mobileappws.service.impl.UserServiceImpl;
import com.example.mobileappws.shared.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import static org.springframework.beans.BeanUtils.copyProperties;

@RestController
@RequestMapping("/users/")
public class UserController {

    UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUser() {
        return "get user was called";
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {

        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        copyProperties(createdUser, returnValue);

        return returnValue;
    }

    @PutMapping
    public String updateUser() {
        return "update user was called";
    }

    @DeleteMapping
    public String deleteUser() {
        return "delete user was called";
    }
}
