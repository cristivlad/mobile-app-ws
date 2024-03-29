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

    @Test
    final void testFindUserByLastName() {
        List<UserEntity> test = userRepository.findUserByLastName("Test");

        assertNotNull(test);
        assertEquals(userEntity.getLastName(), test.get(0).getLastName());
    }

    @Test
    final void testFindUsersByKeyword() {
        List<UserEntity> te = userRepository.findUsersByKeyword("Te");

        assertNotNull(te);
        assertTrue(te.get(0).getFirstName().contains("Te") || te.get(0).getLastName().contains("Te"));
    }

    @Test
    final void testFirstNameAndLastNameByKeyword() {
        List<Object[]> te = userRepository.findUserFirstNameAndLastNameByKeyword("Te");
        String userFirstName = String.valueOf(te.get(0)[0]);
        String userLastName = String.valueOf(te.get(0)[1]);

        assertNotNull(te);
        assertNotNull(userFirstName);
        assertNotNull(userLastName);
    }

    @Test
    final void testUpdateEmailVerificationStatus() {
        userRepository.updateUserEmailVerificationStatus(false, "123Test");

        UserEntity byUserId = userRepository.findByUserId("123Test");
        assertFalse(byUserId.getEmailVerificationStatus());
    }

    @Test
    final void testFindUserByUserId() {
        UserEntity uSerEntityByUserId = userRepository.findUSerEntityByUserId("123Test");

        assertNotNull(uSerEntityByUserId);
    }

    @Test
    final void testGetFullNameByUserId() {
        List<Object[]> userEntityFullNameById = userRepository.getUserEntityFullNameById("123Test");
        String firstName = String.valueOf(userEntityFullNameById.get(0)[0]);
        String lastName = String.valueOf(userEntityFullNameById.get(0)[1]);

        assertNotNull(userEntityFullNameById);
        assertNotNull(firstName);
        assertNotNull(lastName);
    }

    @AfterEach
    void afterEach() {
        userRepository.delete(userEntity);
    }
}