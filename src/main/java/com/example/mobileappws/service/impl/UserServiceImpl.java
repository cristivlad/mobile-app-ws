package com.example.mobileappws.service.impl;

import com.example.mobileappws.entity.PasswordResetTokenEntity;
import com.example.mobileappws.entity.UserEntity;
import com.example.mobileappws.exceptions.DataNotFoundException;
import com.example.mobileappws.repository.PasswordResetTokenRepository;
import com.example.mobileappws.repository.UserRepository;
import com.example.mobileappws.service.UserService;
import com.example.mobileappws.shared.AmazonSES;
import com.example.mobileappws.shared.Utils;
import com.example.mobileappws.shared.dto.AddressDto;
import com.example.mobileappws.shared.dto.UserDto;
import lombok.RequiredArgsConstructor;
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
import static java.lang.Boolean.TRUE;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final Utils utils;

    @Override
    public UserDto createUser(UserDto userDto) {

        if(userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new DataNotFoundException("Email already exists");
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
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(false);

        UserEntity storedUserDetails = userRepository.save(userEntity);
        UserDto returnedUserDto = mapper.map(storedUserDetails, UserDto.class);
        new AmazonSES().verifyEmail(returnedUserDto);

        return returnedUserDto;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if(userEntity == null)
            throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), userEntity.getEmailVerificationStatus(),
                true, true, true, new ArrayList<>());
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

    @Override
    public boolean verifyEmailToken(String token) {
        var entity = userRepository.findUserByEmailVerificationToken(token);

        if (entity != null) {
            boolean hasTokenExpired = utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                entity.setEmailVerificationToken(null);
                entity.setEmailVerificationStatus(TRUE);
                userRepository.save(entity);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean requestPasswordReset(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            return false;
        }
        String token = utils.generatePasswordResetToken(userEntity.getUserId());

        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
        passwordResetTokenEntity.setToken(token);
        passwordResetTokenEntity.setUserDetails(userEntity);
        passwordResetTokenRepository.save(passwordResetTokenEntity);

        return new AmazonSES().sendPasswordResetRequest(userEntity.getFirstName(), userEntity.getEmail(), token);
    }

    @Override
    public boolean resetPassword(String token, String password) {
        if (utils.hasTokenExpired(token))
            return false;

        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);
        if (passwordResetTokenEntity == null)
            return false;

        String encodedPassword = bCryptPasswordEncoder.encode(password);

        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedEntity = userRepository.save(userEntity);

        boolean returnValue = savedEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword);

        passwordResetTokenRepository.delete(passwordResetTokenEntity);
        return returnValue;
     }

}
