package com.SrMarxx.archive.business.models.entities;

import com.SrMarxx.archive.infrastructure.enums.StatusFile;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "TB_FILES")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "file_id")
    private Long fileId;
    private String name;
    private String downloadUri;
    @ManyToOne
    private UserEntity user;
    @CreationTimestamp
    private Instant uploadDateFile;
    private StatusFile statusFile;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Instant getUploadDateFile() {
        return uploadDateFile;
    }

    public void setUploadDateFile(Instant uploadDateFile) {
        this.uploadDateFile = uploadDateFile;
    }

    public StatusFile getStatusFile() {
        return statusFile;
    }

    public void setStatusFile(StatusFile statusFile) {
        this.statusFile = statusFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }
}

