package com.SrMarxx.archive.infrastructure.configs;


import com.SrMarxx.archive.business.models.entities.RoleEntity;
import com.SrMarxx.archive.business.models.entities.UserEntity;
import com.SrMarxx.archive.business.models.repositories.IRoleJpaRepository;
import com.SrMarxx.archive.business.models.repositories.IUserJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final IRoleJpaRepository roleJpaRepository;
    private final IUserJpaRepository userJpaRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(IRoleJpaRepository roleJpaRepository, IUserJpaRepository userJpaRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleJpaRepository = roleJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        var roleAdmin = roleJpaRepository.findByName(RoleEntity.Values.ADMIN.name());

        var userAdmin = userJpaRepository.findByUsername("admin");

        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("admin ja existe");
                    },
                () -> {
                    var user = new UserEntity();
                    user.setUsername("admin");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setRoles(Set.of(roleAdmin));
                    userJpaRepository.save(user);
                }
        );
    }
}
