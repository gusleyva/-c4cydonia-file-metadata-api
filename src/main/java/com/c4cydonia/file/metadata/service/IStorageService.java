package com.c4cydonia.file.metadata.service;

import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {
    String constructFileUrl(String uuid, String originalFileName);
    String saveFile(String fileId, MultipartFile file);
    byte[] downloadFile(String fileId);
    void updateFile(String fileId, MultipartFile file);
    boolean deleteFile(String path);
}
