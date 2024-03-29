package com.example.mobileappws.repository.user;

import com.example.mobileappws.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    UserEntity findUserByEmailVerificationToken(String token);

    @Query(value = "SELECT u.* from users u WHERE u.email_verification_status is true",
            countQuery = "SELECT count(u.*) from users u WHERE u.email_verification_status is true", nativeQuery = true)
    Page<UserEntity> findAllUserWithConfirmedEmailAddress(Pageable pageableRequest);

    @Query(value = "SELECT * FROM users u where u.first_name = ?1", nativeQuery = true)
    List<UserEntity> findUserByFirstName(String firstName);

    @Query(value = "SELECT * FROM users u WHERE u.last_name = :lastName", nativeQuery = true)
    List<UserEntity> findUserByLastName(@Param("lastName") String lastName);

    @Query(value = "SELECT * FROM users u WHERE u.first_name like %:keyword% OR u.last_name like %:keyword%", nativeQuery = true)
    List<UserEntity> findUsersByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT u.first_name, u.last_name FROM users u WHERE u.first_name like %:keyword% OR u.last_name like %:keyword%", nativeQuery = true)
    List<Object[]> findUserFirstNameAndLastNameByKeyword(@Param("keyword") String keyword);

    @Modifying
    @Transactional
    @Query(value = "update users u set u.email_verification_status = :emailVerificationStatus WHERE u.user_id = :userId", nativeQuery = true)
    void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus, @Param("userId") String userId);

    @Query(value = "select u from UserEntity u where u.userId = :userId", nativeQuery = false)
    UserEntity findUSerEntityByUserId(@Param("userId") String userId);

    @Query("select u.firstName, u.lastName from UserEntity u where u.userId = :userId")
    List<Object[]> getUserEntityFullNameById(@Param("userId") String userId);
}
