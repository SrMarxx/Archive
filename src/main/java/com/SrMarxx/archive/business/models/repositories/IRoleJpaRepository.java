package com.SrMarxx.archive.business.models.repositories;

import com.SrMarxx.archive.business.models.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleJpaRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByName(String name);
}
