package com.c4cydonia.file.metadata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FileUploadRequest {
    @NotNull(message = "File is mandatory")
    private MultipartFile file;

    @Valid
    @NotNull(message = "Metadata is mandatory")
    private FileMetadataRequestDto metadata;
}
