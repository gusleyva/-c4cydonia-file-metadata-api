package com.c4cydonia.file.metadata.service;

import com.c4cydonia.file.metadata.model.FileMetadataRequestDto;
import com.c4cydonia.file.metadata.model.FileMetadataResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    FileMetadataResponseDto uploadFile(MultipartFile file, String createdBy, FileMetadataRequestDto fileMetadataDto);
    FileMetadataResponseDto retrieveFileMetadata(String fileId, String requesterEmail);
    FileMetadataResponseDto updateFileMetadata(String fileId, FileMetadataRequestDto updates, String requesterEmail);
    void deleteFile(String fileId, String requesterEmail);
}
