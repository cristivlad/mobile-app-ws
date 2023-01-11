package com.example.mobileappws;

import com.example.mobileappws.entity.AuthorityEntity;
import com.example.mobileappws.entity.RoleEntity;
import com.example.mobileappws.entity.UserEntity;
import com.example.mobileappws.repository.AuthorityRepository;
import com.example.mobileappws.repository.RoleRepository;
import com.example.mobileappws.repository.user.UserRepository;
import com.example.mobileappws.shared.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitialUserSetup {
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final Utils utils;
    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        RoleEntity roleUser = createRole("ROLE_USER", List.of(readAuthority, writeAuthority));
        RoleEntity roleAdmin = createRole("ROLE_ADMIN", List.of(readAuthority, writeAuthority, deleteAuthority));

        if (roleAdmin == null) return;

        UserEntity adminUser = new UserEntity();
        adminUser.setFirstName("Cristi");
        adminUser.setLastName("V");
        adminUser.setEmail("test@test.com");
        adminUser.setEmailVerificationStatus(true);
        adminUser.setUserId(utils.generateUserId(30));
        adminUser.setEncryptedPassword(encoder.encode("12345"));
        adminUser.setRoles(List.of(roleAdmin));

        userRepository.save(adminUser);
    }

    @Transactional
    public RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
        RoleEntity role = roleRepository.findByName(name);

        if (role == null) {
            role = new RoleEntity();
            role.setName(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }
        return role;
    }

    @Transactional
    public AuthorityEntity createAuthority(String name) {
        AuthorityEntity authority = authorityRepository.findByName(name);

        if (authority == null) {
            authority = new AuthorityEntity();
            authority.setName(name);
            authorityRepository.save(authority);
        }
        return authority;
    }
}
