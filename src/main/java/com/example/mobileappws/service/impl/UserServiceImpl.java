package com.example.mobileappws.service.impl;

import com.example.mobileappws.entity.UserEntity;
import com.example.mobileappws.repository.UserRepository;
import com.example.mobileappws.service.UserService;
import com.example.mobileappws.shared.Utils;
import com.example.mobileappws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    Utils utils;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, Utils utils,BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        if(userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        UserEntity userEntity = new UserEntity();
        copyProperties(userDto, userEntity);

        String publicUserId = utils.generateUserId(30);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setUserId(publicUserId);

        UserEntity storedUserDetails = userRepository.save(userEntity);

        UserDto returnValue = new UserDto();
        copyProperties(storedUserDetails, returnValue);

        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null)
            throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), emptyList());
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null)
            throw new UsernameNotFoundException(email);
        UserDto returnValue = new UserDto();
        copyProperties(userEntity, returnValue);

        return returnValue;
    }
}
