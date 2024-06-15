package com.c4cydonia.file.metadata.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileMetadataRequestDto {
    @NotBlank(message = "Filename is mandatory.")
    private String fileName;
    private String text;
    private String title;

    @Valid
    private OwnershipRequestDto ownershipDetails;
}
