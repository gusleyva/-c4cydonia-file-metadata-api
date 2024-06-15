package com.c4cydonia.file.metadata.repository;

import com.c4cydonia.file.metadata.model.FileMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository extends MongoRepository<FileMetadata, String> {
    Optional<FileMetadata> findByFileId(String fileId);

    List<FileMetadata> findByCreatedBy(String createdBy);

    Optional<FileMetadata> findByFileName(String fileName);
}
