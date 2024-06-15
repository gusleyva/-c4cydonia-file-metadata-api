package com.c4cydonia.file.metadata.service.impl;

import com.c4cydonia.file.metadata.exception.FileException;
import com.c4cydonia.file.metadata.model.FileMetadata;
import com.c4cydonia.file.metadata.model.FileMetadataRequestDto;
import com.c4cydonia.file.metadata.model.FileMetadataResponseDto;
import com.c4cydonia.file.metadata.model.OwnershipDetails;
import com.c4cydonia.file.metadata.repository.FileMetadataRepository;
import com.c4cydonia.file.metadata.service.IFileService;
import com.c4cydonia.file.metadata.service.IStorageService;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileService implements IFileService {

    private FileMetadataRepository fileRepository;
    private IStorageService storageService;
    private ModelMapper modelMapper;

    private final Map<String, Set<String>> allowedFileTypes = Map.of(
            "image", Set.of("bmp", "dib", "gif", "jpg", "jpeg"), //, "png"
            "text", Set.of("txt", "csv")
    );

    @Override
    public FileMetadataResponseDto uploadFile(MultipartFile file, String createdBy, FileMetadataRequestDto fileMetadataDto) {
        validateFile(file, createdBy);

        var ownership = OwnershipDetails.builder()
                .addedBy(createdBy)
                .owners(fileMetadataDto.getOwnershipDetails().getOwners())
                .receivers(fileMetadataDto.getOwnershipDetails().getReceivers())
                .build();

        // TODO - Handle save conflicts
        String uuid = UUID.randomUUID().toString();

        String fileUrl = storageService.constructFileUrl(uuid, file.getOriginalFilename());

        var instantNow = Instant.now();
        FileMetadata fileMetadata = FileMetadata.builder()
                .fileId(uuid)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .createdBy(createdBy)
                .text(fileMetadataDto.getText())
                .title(fileMetadataDto.getTitle())
                .fileUrl(fileUrl)
                .ownershipDetails(ownership)
                .createdDate(instantNow)
                .modifiedDate(instantNow)
                .build();

        var fileMetadataResponseDb = fileRepository.save(fileMetadata);

        var fileMetadataResponse = modelMapper.map(fileMetadataResponseDb, FileMetadataResponseDto.class);

        return fileMetadataResponse;
    }

    private void validateFile(MultipartFile file, String createdBy) {
        if (file == null || file.isEmpty()) {
            throw new FileException(HttpStatus.BAD_REQUEST, "No file provided");
        }

        if (createdBy == null || createdBy.trim().isEmpty()) {
            throw new FileException(HttpStatus.BAD_REQUEST, "No user provided");
        }

        if (!isValidFileType(file)) {
            throw new FileException(HttpStatus.NOT_ACCEPTABLE, "Invalid file type");
        }
    }

    public boolean isValidFileType(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);
        String mediaType = getMediaType(file);

        return isTypeAllowed(mediaType, extension);
    }

    public String getMediaType(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        try {
            return new Tika().detect(TikaInputStream.get(multipartFile.getInputStream()), fileName);
        } catch (IOException e) {
            throw new FileException(HttpStatus.NOT_ACCEPTABLE, "Issue with file type detection");
        }
    }

    private boolean isTypeAllowed(String mediaType, String fileExtension) {
        String category = mediaType.split("/")[0];

        if (allowedFileTypes.containsKey(category)) {
            return allowedFileTypes.get(category).contains(fileExtension);
        }
        return false;
    }

    @Override
    public FileMetadataResponseDto retrieveFileMetadata(String fileId, String requesterEmail) {
        FileMetadata fileMetadata = retrieveFile(fileId);
        validateOwnership(fileMetadata, requesterEmail);
        var fileMetadataResponse = modelMapper.map(fileMetadata, FileMetadataResponseDto.class);
        return fileMetadataResponse;
    }

    // Avoid duplicate code
    private FileMetadata retrieveFile(String fileId) {
        return fileRepository.findByFileId(fileId)
                .orElseThrow(() -> new FileException(HttpStatus.NOT_FOUND, "File not found"));
    }

    // TODO - Search by bulk
    // TODO - This will help with a possible test where some files were found and others not

    private void validateOwnership(FileMetadata fileMetadata, String requesterEmail) {
        var ownershipDetails = fileMetadata.getOwnershipDetails();

        if (Objects.isNull(ownershipDetails)
                && !fileMetadata.getCreatedBy().equalsIgnoreCase(requesterEmail) ) {
            throw new FileException(HttpStatus.FORBIDDEN, "Unauthorized access");
        }

        var isCreator = fileMetadata.getCreatedBy().equalsIgnoreCase(requesterEmail);
        var isAddedBy = !Objects.isNull(ownershipDetails.getAddedBy())
                && ownershipDetails.getAddedBy().equalsIgnoreCase(requesterEmail);
        var isOwner = !Objects.isNull(ownershipDetails.getOwners())
                && ownershipDetails.getOwners().contains(requesterEmail);
        var isReceiver = !Objects.isNull(ownershipDetails.getReceivers())
                && ownershipDetails.getReceivers().contains(requesterEmail);
        if (!isCreator && !isAddedBy && !isOwner && !isReceiver) {
            throw new FileException(HttpStatus.FORBIDDEN, "Unauthorized access");
        }
    }

    @Override
    public FileMetadataResponseDto updateFileMetadata(String fileId, FileMetadataRequestDto updates, String requesterEmail) {
        FileMetadata fileMetadata = retrieveFile(fileId);

        validateOwnership(fileMetadata, requesterEmail);

        var ownerShipDetailsDto = updates.getOwnershipDetails();
        var ownershipDetails = modelMapper.map(ownerShipDetailsDto, OwnershipDetails.class);

        fileMetadata.setText(updates.getText());
        fileMetadata.setTitle(updates.getTitle());
        fileMetadata.setOwnershipDetails(ownershipDetails);
        fileMetadata.setModifiedDate(Instant.now());

        fileRepository.save(fileMetadata);
        var fileMetadataResponse = modelMapper.map(fileMetadata, FileMetadataResponseDto.class);

        return fileMetadataResponse;
    }

    @Override
    public void deleteFile(String fileId, String requesterEmail) {
        FileMetadata fileMetadata = retrieveFile(fileId);
        validateOwnership(fileMetadata, requesterEmail);

        // Simulate call to AWS S3 to DELETE the file
        var isDeleted = storageService.deleteFile(fileMetadata.getFileId());
        if (!isDeleted) {
            throw new FileException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file");
        }
        fileRepository.deleteById(fileMetadata.getId());
    }

    // TODO - Add a method to download the file? UI can handle that part with the URL
}


