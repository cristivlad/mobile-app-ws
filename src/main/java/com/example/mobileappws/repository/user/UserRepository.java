package com.example.mobileappws.repository.user;

import com.example.mobileappws.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    UserEntity findUserByEmailVerificationToken(String token);

    @Query(value = "SELECT u.* from users u WHERE u.email_verification_status is true",
            countQuery = "SELECT count(u.*) from users u WHERE u.email_verification_status is true", nativeQuery = true)
    Page<UserEntity> findAllUserWithConfirmedEmailAddress(Pageable pageableRequest);
}
