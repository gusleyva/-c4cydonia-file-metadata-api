package com.c4cydonia.file.metadata;

import com.c4cydonia.file.metadata.config.MapperConfig;
import com.c4cydonia.file.metadata.exception.FileException;
import com.c4cydonia.file.metadata.model.*;
import com.c4cydonia.file.metadata.repository.FileMetadataRepository;
import com.c4cydonia.file.metadata.service.IStorageService;
import com.c4cydonia.file.metadata.service.impl.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    private static final String IMG_DOG = "imgs/dog.jpg";
    private static final String IMG_SOLID = "imgs/solid.png";

    private static final String FILE_NAME = "file";
    private static final String FILE_TEST = "test.jpg";
    private static final String TEXT = "Testing text test";
    private static final String TITLE = "Title test";

    private static final String TYPE_IMG = "image/jpeg";

    private static final String USER_1 = "user@example.com";
    private static final String USER_CREATOR = "creator@example.com";
    private static final String USER_OWNER = "owner@example.com";



    @Mock
    private FileMetadataRepository fileRepository;

    @Mock
    private IStorageService storageService;

    private ModelMapper modelMapper = new MapperConfig().modelMapper();

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService(fileRepository, storageService, modelMapper);
    }

    @Test
    void uploadFile_ValidFile_ReturnsMetadataResponse() throws Exception {
        File imageFile = Paths.get(ClassLoader.getSystemResource(IMG_DOG).toURI()).toFile();
        var mockFile = convertFileToMultipartFile(imageFile);

        var ownership = buildOwnershipRequest(Set.of(USER_1), Collections.emptySet());
        var requestDto = buildFileMetadataRequestDto(FILE_TEST, TEXT, TITLE, ownership);
        FileMetadata fileMetadata = new FileMetadata();

        lenient().when(storageService.constructFileUrl(anyString(), anyString()))
                .thenReturn("http://example.com/test.jpg");
        lenient().when(fileRepository.save(any(FileMetadata.class)))
                .thenReturn(fileMetadata);

        FileMetadataResponseDto result = fileService.uploadFile(mockFile, "user@example.com", requestDto);

        assertNotNull(result);
        verify(fileRepository).save(any(FileMetadata.class));
    }

    private MultipartFile convertFileToMultipartFile(File file) throws Exception {
        Path path = file.toPath();
        String contentType = Files.probeContentType(path);
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile(file.getName(), file.getName(), contentType, content);
    }

    private OwnershipRequestDto buildOwnershipRequest(Set<String> owners, Set<String> receivers) {
        return OwnershipRequestDto.builder()
                .owners(owners)
                .receivers(receivers)
                .build();
    }

    private FileMetadataRequestDto buildFileMetadataRequestDto(String fileName, String text, String title, OwnershipRequestDto ownership) {
        return FileMetadataRequestDto.builder()
                .fileName(fileName)
                .text(text)
                .title(title)
                .ownershipDetails(ownership)
                .build();
    }

    @Test
    void uploadFile_EmptyFile_ThrowsException() {
        MultipartFile emptyFile = new MockMultipartFile(FILE_NAME, FILE_TEST, TYPE_IMG, new byte[0]);

        Exception exception = assertThrows(FileException.class, () -> {
            fileService.uploadFile(emptyFile, USER_1, new FileMetadataRequestDto());
        });

        assertEquals("No file provided", exception.getMessage());
    }

    @Test
    void uploadFile_InvalidFileType_ThrowsException() throws Exception {
        File imageFile = Paths.get(ClassLoader.getSystemResource(IMG_SOLID).toURI()).toFile();
        var invalidFile = convertFileToMultipartFile(imageFile);

        var ownership = buildOwnershipRequest(Set.of(USER_1), Collections.emptySet());
        var requestDto = buildFileMetadataRequestDto(FILE_TEST, TEXT, TITLE, ownership);

        Exception exception = assertThrows(FileException.class, () -> {
            fileService.uploadFile(invalidFile, USER_1, requestDto);
        });

        assertEquals("Invalid file type", exception.getMessage());
    }

    @Test
    void retrieveFileMetadata_AuthorizedAccess_ReturnsFileMetadata() {
        String fileId = "authorized-file-id";
        var ownership = buildOwnership(Set.of(USER_CREATOR, USER_OWNER), Set.of(), USER_CREATOR);

        FileMetadata fileMetadata = FileMetadata.builder()
                .fileId(fileId)
                .createdBy(USER_CREATOR)
                .ownershipDetails(ownership)
                .build();

        when(fileRepository.findByFileId(fileId))
                .thenReturn(Optional.of(fileMetadata));

        FileMetadataResponseDto result = fileService.retrieveFileMetadata(fileId, USER_CREATOR);

        assertNotNull(result);
        // Validar createdBy o validar ownershipDetails
        // Validar fileName, fileURL
    }

    private OwnershipDetails buildOwnership(Set<String> owners, Set<String> receivers, String addedBy) {
        return OwnershipDetails.builder()
                .owners(owners)
                .receivers(receivers)
                .addedBy(addedBy)
                .build();
    }

    @Test
    void updateFileMetadata_ValidRequest_UpdatesSuccessfully() {
        String fileId = "updatable-file-id";
        String updatedText = "Updated Text";
        String updatedTitle = "Updated Title";
        var ownership = buildOwnership(Set.of(USER_CREATOR, USER_OWNER), Set.of("authorized@example.com"), USER_CREATOR);

        FileMetadata existingMetadata = FileMetadata.builder()
                .fileId(fileId)
                .createdBy(USER_CREATOR)
                .ownershipDetails(ownership)
                .build();

        FileMetadataRequestDto updates = FileMetadataRequestDto.builder()
                .text(updatedText)
                .title(updatedTitle)
                .ownershipDetails(OwnershipRequestDto.builder().build())
                .build();

        when(fileRepository.findByFileId(fileId)).thenReturn(Optional.of(existingMetadata));
        when(fileRepository.save(any(FileMetadata.class))).thenReturn(existingMetadata);

        FileMetadataResponseDto result = fileService.updateFileMetadata(fileId, updates, USER_OWNER);

        assertNotNull(result);
        verify(fileRepository).save(existingMetadata);
        assertEquals(updatedText, existingMetadata.getText());
        assertEquals(updatedTitle, existingMetadata.getTitle());
        // assertEquals(validate owners
        // assertEquals(validate receivers
    }

    @Test
    void deleteFile_UnauthorizedUser_ThrowsException() {
        String fileId = "deletable-file-id";
        var ownership = buildOwnership(Set.of(USER_CREATOR, USER_OWNER), Set.of(), USER_CREATOR);

        var fileMetadata = FileMetadata.builder()
                .createdBy(USER_CREATOR)
                .ownershipDetails(ownership)
                .build();

        when(fileRepository.findByFileId(fileId))
                .thenReturn(Optional.of(fileMetadata));

        Exception exception = assertThrows(FileException.class, () -> {
            fileService.deleteFile(fileId, "unauthorized@example.com");
        });

        assertEquals("Unauthorized access", exception.getMessage());
    }

    @Test
    void deleteFile_AuthorizedUser_DeletesSuccessfully() {
        String fileId = "deletable-file-id";

        var ownership = buildOwnership(Set.of(USER_CREATOR, USER_OWNER), Set.of(), "creator@example.com");

        var fileMetadata = FileMetadata.builder()
                .id("123")
                .fileId(fileId)
                .createdBy(USER_CREATOR)
                .ownershipDetails(ownership)
                .build();

        when(storageService.deleteFile(anyString())).thenReturn(true);
        when(fileRepository.findByFileId(fileId))
                .thenReturn(Optional.of(fileMetadata));

        fileService.deleteFile(fileId, USER_OWNER);

        verify(fileRepository).deleteById(anyString());
    }

    // TODO - Test - throw new FileException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file");
}
