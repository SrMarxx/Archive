package com.SrMarxx.archive.business.models.repositories;

import com.SrMarxx.archive.business.models.entities.UserEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IUserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(@NotBlank String username);
}
