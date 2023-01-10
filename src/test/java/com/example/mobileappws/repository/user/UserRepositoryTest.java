package com.example.mobileappws.repository.user;

import com.example.mobileappws.entity.AddressEntity;
import com.example.mobileappws.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    private UserEntity userEntity;
    @BeforeEach
    void setUp() {
        AddressEntity addressEntity = AddressEntity.builder()
                .type("shipping")
                .addressId("12341234Test")
                .city("TestCity")
                .country("TestCountry")
                .postalCode("POST1")
                .streetName("StreetTest")
                .build();
        userEntity = UserEntity.builder()
                .firstName("Test")
                .lastName("Test")
                .userId("123Test")
                .encryptedPassword("xxx")
                .email("test@test.com")
                .emailVerificationStatus(true)
                .addresses(List.of(addressEntity))
                .build();
        userRepository.save(userEntity);
    }

    @Test
    final void testGetVerifiedUsers() {
        Pageable pageable = PageRequest.of(0,2);
        Page<UserEntity> allUserWithConfirmedEmailAddress = userRepository.findAllUserWithConfirmedEmailAddress(pageable);

        assertNotNull(allUserWithConfirmedEmailAddress);
    }

    @Test
    final void testFindUSerByFirstName() {
        List<UserEntity> test = userRepository.findUserByFirstName("Test");

        assertNotNull(test);
        assertEquals(userEntity.getFirstName(), test.get(0).getFirstName());
    }

    @AfterEach
    void afterEach() {
        userRepository.delete(userEntity);
    }
}