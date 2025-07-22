package com.SrMarxx.archive.business.models.repositories;

import com.SrMarxx.archive.business.models.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IFileJpaRepository extends JpaRepository <FileEntity, Long>{
}
