package com.example.mobileappws.service.impl;

import com.example.mobileappws.entity.UserEntity;
import com.example.mobileappws.repository.UserRepository;
import com.example.mobileappws.service.UserService;
import com.example.mobileappws.shared.Utils;
import com.example.mobileappws.shared.dto.AddressDto;
import com.example.mobileappws.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.mobileappws.model.response.ErrorMessage.NO_RECORD_FOUND;
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

        for (int i = 0; i < userDto.getAddresses().size(); i++) {
            AddressDto addressDto = userDto.getAddresses().get(i);
            addressDto.setUserDetails(userDto);
            addressDto.setAddressId(utils.generateAddressId(30));
            userDto.getAddresses().set(i, addressDto);
        }
        ModelMapper mapper = new ModelMapper();
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setUserId(publicUserId);

        UserEntity storedUserDetails = userRepository.save(userEntity);

        return mapper.map(storedUserDetails, UserDto.class);
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

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            throw new UsernameNotFoundException(NO_RECORD_FOUND.getError());

        UserDto returnValue = new UserDto();
        copyProperties(userEntity, returnValue);

        return returnValue;
    }

    @Transactional
    @Override
    public UserDto updateUser(String userId, UserDto userDto) {
        UserDto returnValue = new UserDto();
        UserDto userByUserId = getUserByUserId(userId);

        userByUserId.setFirstName(userDto.getFirstName());
        userByUserId.setLastName(userDto.getLastName());

        UserEntity entity = new UserEntity();
        copyProperties(userByUserId, entity);

        UserEntity updatedUser = userRepository.save(entity);

        copyProperties(updatedUser, returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null)
            throw new UsernameNotFoundException(NO_RECORD_FOUND.getError());

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<>();
        Pageable pageableRequest = PageRequest.of(page, limit);

        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        List<UserEntity> users = usersPage.getContent();

        for (UserEntity entity : users) {
            UserDto userDto = new UserDto();
            copyProperties(entity, userDto);
            returnValue.add(userDto);
        }
        return returnValue;
    }
}
