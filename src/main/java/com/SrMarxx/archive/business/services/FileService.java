package com.SrMarxx.archive.business.services;


import com.SrMarxx.archive.business.models.entities.FileEntity;
import com.SrMarxx.archive.business.models.entities.UserEntity;
import com.SrMarxx.archive.business.models.repositories.IFileJpaRepository;
import com.SrMarxx.archive.infrastructure.configs.FileStoragePropertiesConfig;
import com.SrMarxx.archive.infrastructure.enums.StatusFile;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    IFileJpaRepository fileRepository;

    @Autowired
    FileStoragePropertiesConfig fileStoragePropertiesConfig;

    public boolean saveFile(MultipartFile file, UserEntity user) throws IOException {
        var fileEntity = new FileEntity();
        var fileStorageLocation = Paths.get(fileStoragePropertiesConfig.getUploadDir() +"/"+ user.getUserId().toString()).toAbsolutePath().normalize();
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        Optional<String> fileNames = Files.list(fileStorageLocation)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(name -> name.equals(fileName))
                .findFirst();

        if (fileNames.isPresent()) {
            String fileNameWithoutExt = FilenameUtils.removeExtension(fileName);
            String oldFileName = fileNameWithoutExt;
            String newFileName = null;
            for (int i = 1; fileNames.isPresent(); i++) {
                oldFileName = fileNameWithoutExt + "(" + i + ")";
                newFileName = oldFileName + "." + FilenameUtils.getExtension(fileName);
                String finalNewFileName = newFileName;
                fileNames = Files.list(fileStorageLocation)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .filter(name -> name.equals(finalNewFileName))
                        .findFirst();
            }
            newFileName = oldFileName + "." + FilenameUtils.getExtension(fileName);

            fileEntity = new FileEntity();

            fileEntity.setUser(user);
            fileEntity.setName(newFileName);
        } else {

            fileEntity = new FileEntity();

            fileEntity.setUser(user);
            fileEntity.setName(fileName);
        }

        try {
            Path targetLocation = fileStorageLocation.resolve(fileEntity.getName());
            file.transferTo(targetLocation);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(fileName)
                    .toUriString();

            fileEntity.setDownloadUri(fileDownloadUri);
            fileEntity.setStatusFile(StatusFile.AVAILABLE);

            fileRepository.save(fileEntity);


            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
