package com.SrMarxx.archive.controllers;

import com.SrMarxx.archive.business.models.repositories.IUserJpaRepository;
import com.SrMarxx.archive.business.services.FileService;
import com.SrMarxx.archive.infrastructure.configs.FileStoragePropertiesConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/api/files")
public class FileStorageController {
    private final FileStoragePropertiesConfig fileStoragePropertiesConfig;
    private final IUserJpaRepository userRepository;
    private final FileService fileService;

    public FileStorageController(FileStoragePropertiesConfig fileStoragePropertiesConfig, IUserJpaRepository userRepository, FileService fileService) {
        this.fileStoragePropertiesConfig = fileStoragePropertiesConfig;
        this.userRepository = userRepository;
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file, JwtAuthenticationToken token) throws IOException {

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()){
            log.warn("Upload recebido sem nome de arquivo original.");
            return ResponseEntity.badRequest().build();
        }

        var user = userRepository.findById(UUID.fromString(token.getName()));
        var response = fileService.saveFile(file, user.get());

        if (!response){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request, JwtAuthenticationToken token) throws IOException {
        var user = userRepository.findById(UUID.fromString(token.getName()));

        var fileStorageLocation = Paths.get(fileStoragePropertiesConfig.getUploadDir() +"/"+ user.get().getUserId().toString()).toAbsolutePath().normalize();


        Path filePath = fileStorageLocation.resolve(fileName).normalize();

        try{
            Resource resource = new UrlResource(filePath.toUri());
            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

            if (contentType == null){
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment: filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles(JwtAuthenticationToken token) throws IOException{

        var user = userRepository.findById(UUID.fromString(token.getName()));

        var fileStorageLocation = Paths.get(fileStoragePropertiesConfig.getUploadDir() +"/"+ user.get().getUserId().toString()).toAbsolutePath().normalize();


        List<String> fileNames = Files.list(fileStorageLocation)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());

        return ResponseEntity.ok(fileNames);
    }


}
