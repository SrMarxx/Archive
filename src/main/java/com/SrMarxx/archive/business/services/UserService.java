package com.SrMarxx.archive.business.services;


import com.SrMarxx.archive.business.models.entities.UserEntity;
import com.SrMarxx.archive.business.models.repositories.IUserJpaRepository;
import com.SrMarxx.archive.infrastructure.configs.FileStoragePropertiesConfig;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class UserService {

    @Autowired
    IUserJpaRepository userRepository;

    private final Path fileStorageLocation;

    public UserService(FileStoragePropertiesConfig fileStoragePropertiesConfig) {
        this.fileStorageLocation = Paths.get(fileStoragePropertiesConfig.getUploadDir()).toAbsolutePath().normalize();
    }

    @Transactional
    public UserEntity save(UserEntity userEntity) {
        log.info("Iniciando processo de salvar um novo usuário");
        userEntity = userRepository.save(userEntity);
        log.info("Novo usuário salvo no banco de dados");

        log.info("Iniciando a criação da pasta de uploads do usuário");
        File newDir = new File(fileStorageLocation + File.separator + userEntity.getUserId());
        if(!newDir.exists()){
            newDir.mkdirs();
            log.info("Pasta criada com sucesso!");
        }

        return userEntity;
    }
}
