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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceParamTest {
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

    @Captor
    private ArgumentCaptor<FileMetadata> metadataCaptor;


    private ModelMapper modelMapper = new MapperConfig().modelMapper();

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService(fileRepository, storageService, modelMapper);
    }

    @ParameterizedTest
    @MethodSource("uploadExceptionTestData")
    void uploadFileExceptions(MultipartFile file, String user, FileMetadataRequestDto requestDto,
                              FileMetadata fileMetadata, String expectedMessage) {
        lenient().when(fileRepository.findById(anyString())).thenReturn(Optional.of(fileMetadata));

        Exception exception = assertThrows(FileException.class, () -> {
            fileService.uploadFile(file, user, requestDto);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    static Stream<Arguments> uploadExceptionTestData() throws Exception {
        var emptyFileMetadata = new FileMetadata();
        var emptyFileMetadataRequest = new FileMetadataRequestDto();

        var ownershipRequestDto = buildOwnershipRequest(Set.of(USER_1), Collections.emptySet());
        var fileMetadataRequestDto = buildFileMetadataRequestDto(FILE_TEST, TEXT, TITLE, ownershipRequestDto);

        MultipartFile emptyFile = new MockMultipartFile(FILE_NAME, FILE_TEST, TYPE_IMG, new byte[0]);

        File imageFile = Paths.get(ClassLoader.getSystemResource(IMG_SOLID).toURI()).toFile();
        var invalidFile = convertFileToMultipartFile(imageFile);

        return Stream.of(
                Arguments.of(emptyFile, USER_1, emptyFileMetadataRequest, emptyFileMetadata, "No file provided"),
                Arguments.of(invalidFile, USER_1, fileMetadataRequestDto, emptyFileMetadata, "Invalid file type")
                // Test excel,
                // Test audio,
                // Test video
        );
    }

    private static FileMetadata buildFileMetadata(OwnershipDetails ownershipDetails) {
        return FileMetadata.builder()
                .ownershipDetails(ownershipDetails)
                .createdBy(USER_CREATOR)
                .build();
    }

    /**
     * TODO - Modify this method as ParameterizedTest to test all valid file uploads.
     *
     * @throws Exception
     */
    @Test
    void uploadFile() throws Exception {
        var fileUrl = "http://example.com/test.jpg";
        File imageFile = Paths.get(ClassLoader.getSystemResource(IMG_DOG).toURI()).toFile();
        var mockFile = convertFileToMultipartFile(imageFile);

        var ownershipDto = buildOwnershipRequest(Set.of(USER_1), Collections.emptySet());
        var requestDto = buildFileMetadataRequestDto(FILE_TEST, TEXT, TITLE, ownershipDto);
        var ownership = modelMapper.map(ownershipDto, OwnershipDetails.class);

        var instantNow = Instant.now();
        FileMetadata fileMetadata = FileMetadata.builder()
                .fileId("UUID-1")
                .fileName(mockFile.getOriginalFilename())
                .fileSize(mockFile.getSize())
                .contentType(mockFile.getContentType())
                .createdBy(USER_CREATOR)
                .text(requestDto.getText())
                .title(requestDto.getTitle())
                .fileUrl(fileUrl)
                .ownershipDetails(ownership)
                .createdDate(instantNow)
                .modifiedDate(instantNow)
                .build();

        lenient().when(storageService.constructFileUrl(anyString(), anyString())).thenReturn(fileUrl);
        lenient().when(fileRepository.save(any(FileMetadata.class))).thenReturn(fileMetadata);

        FileMetadataResponseDto result = fileService.uploadFile(mockFile, "user@example.com", requestDto);

        assertNotNull(result);
        verify(fileRepository).save(any(FileMetadata.class));
        verify(fileRepository, times(1)).save(any(FileMetadata.class));

        assertThat(result.getText()).isEqualTo(TEXT);
        assertThat(result.getOwnershipDetails().getOwners().size()).isEqualTo(1);
        assertTrue(result.getOwnershipDetails().getOwners().contains(USER_1));
    }

    private static MultipartFile convertFileToMultipartFile(File file) throws Exception {
        Path path = file.toPath();
        String contentType = Files.probeContentType(path);
        byte[] content = Files.readAllBytes(path);
        return new MockMultipartFile(file.getName(), file.getName(), contentType, content);
    }

    private static OwnershipRequestDto buildOwnershipRequest(Set<String> owners, Set<String> receivers) {
        return OwnershipRequestDto.builder()
                .owners(owners)
                .receivers(receivers)
                .build();
    }

    private static FileMetadataRequestDto buildFileMetadataRequestDto(String fileName, String text, String title,
                                                                      OwnershipRequestDto ownership) {
        return FileMetadataRequestDto.builder()
                .fileName(fileName)
                .text(text)
                .title(title)
                .ownershipDetails(ownership)
                .build();
    }

    @Test
    void uploadFile_WithDynamicUrl_ReturnsCorrectResponse() throws Exception {
        // Arrange
        File imageFile = Paths.get(ClassLoader.getSystemResource(IMG_DOG).toURI()).toFile();
        MultipartFile mockFile = convertFileToMultipartFile(imageFile);

        OwnershipRequestDto ownershipRequest = buildOwnershipRequest(Set.of(USER_1), Collections.emptySet());
        FileMetadataRequestDto requestDto = buildFileMetadataRequestDto(FILE_TEST, TEXT, TITLE, ownershipRequest);

        // Mocking the dynamic URL construction
        when(storageService.constructFileUrl(anyString(), anyString()))
                .thenAnswer(invocation -> {
                    String uuid = invocation.getArgument(0, String.class);
                    String fileName = invocation.getArgument(1, String.class);
                    return "http://example.com/" + uuid + "/" + fileName;
                });


        // Mocking the fileRepository save method
        Instant now = Instant.now();
        FileMetadata savedFileMetadata = FileMetadata.builder()
                .fileId("123456")
                .fileName(FILE_TEST)
                .fileSize(mockFile.getSize())
                .contentType(mockFile.getContentType())
                .createdBy(USER_1)
                .text(TEXT)
                .title(TITLE)
                .fileUrl("http://example.com/123456/dog.jpg")
                .ownershipDetails(new OwnershipDetails(Set.of(USER_1), Collections.emptySet(), USER_1))
                .createdDate(now)
                .modifiedDate(now)
                .build();

        when(fileRepository.save(any(FileMetadata.class))).thenReturn(savedFileMetadata);

        // Mocking the modelMapper
        FileMetadataResponseDto responseDto = modelMapper.map(savedFileMetadata, FileMetadataResponseDto.class);

        // Act
        var responseFileMetadata = fileService.uploadFile(mockFile, USER_1, requestDto);

        // Assert
        verify(fileRepository).save(metadataCaptor.capture());
        FileMetadata capturedMetadata = metadataCaptor.getValue();
        assertNotNull(capturedMetadata);
        assertEquals("dog.jpg", capturedMetadata.getFileName());
        assertEquals(TEXT, capturedMetadata.getText());
        assertEquals(TITLE, capturedMetadata.getTitle());
        assertEquals(USER_1, capturedMetadata.getCreatedBy());

        // Verify the response
        assertNotNull(responseFileMetadata);
        assertEquals("123456", responseFileMetadata.getFileId());
        assertEquals(FILE_TEST, responseFileMetadata.getFileName());
        assertEquals(mockFile.getSize(), responseFileMetadata.getFileSize());
        assertEquals(mockFile.getContentType(), responseFileMetadata.getContentType());
        assertEquals(now, responseFileMetadata.getCreatedDate());
        assertEquals(now, responseFileMetadata.getModifiedDate());
        assertEquals(USER_1, responseFileMetadata.getCreatedBy());
        assertEquals(TEXT, responseFileMetadata.getText());
        assertEquals(TITLE, responseFileMetadata.getTitle());
        assertEquals(savedFileMetadata.getOwnershipDetails(), responseFileMetadata.getOwnershipDetails());
    }


    @ParameterizedTest
    @MethodSource("deleteExceptionTestData")
    void deleteFileExceptions(String fileId, String user, FileMetadata fileMetadata, String expectedMessage) {
        lenient().when(fileRepository.findByFileId(fileId)).thenReturn(Optional.of(fileMetadata));

        Exception exception = assertThrows(FileException.class, () -> {
            fileService.deleteFile(fileId, user);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    static Stream<Arguments> deleteExceptionTestData() throws Exception {
        var ownership = buildOwnership(Set.of(USER_CREATOR, USER_OWNER), Set.of(), USER_CREATOR);
        var fileMetadataToDelete = buildFileMetadata(ownership);

        return Stream.of(
                Arguments.of("file1", USER_1, fileMetadataToDelete, "Unauthorized access")
        );
    }

    private static OwnershipDetails buildOwnership(Set<String> owners, Set<String> receivers, String addedBy) {
        return OwnershipDetails.builder()
                .owners(owners)
                .receivers(receivers)
                .addedBy(addedBy)
                .build();
    }

    @Test
    void updateFileMetadata() {
        String fileId = "updatable-file-id";
        String updatedText = "Updated Text";
        String updatedTitle = "Updated Title";
        var ownership = buildOwnership(Set.of(USER_CREATOR, USER_OWNER), Set.of("authorized@example.com"), USER_CREATOR);
        var ownershipDto = modelMapper.map(ownership, OwnershipRequestDto.class);

        FileMetadata existingMetadata = FileMetadata.builder()
                .fileId(fileId)
                .createdBy(USER_CREATOR)
                .ownershipDetails(ownership)
                .build();

        FileMetadataRequestDto updates = FileMetadataRequestDto.builder()
                .text(updatedText)
                .title(updatedTitle)
                .ownershipDetails(ownershipDto)
                .build();

        when(fileRepository.findByFileId(fileId)).thenReturn(Optional.of(existingMetadata));
        when(fileRepository.save(any(FileMetadata.class))).thenReturn(existingMetadata);

        FileMetadataResponseDto result = fileService.updateFileMetadata(fileId, updates, USER_OWNER);

        assertNotNull(result);
        verify(fileRepository).save(existingMetadata);
        assertEquals(updatedText, existingMetadata.getText());
        assertEquals(updatedTitle, existingMetadata.getTitle());
    }

    /**
     * The verify method in Mockito is used to ensure that specific interactions or method calls happen
     * on your mocked objects. It's a way to check that certain actions were performed, which can be crucial
     * for ensuring the correct behavior of your system during tests.
     */
    @Test
    void deleteFile() {
        String id = "file-id";
        String fileId = "deletable-file-id";

        var ownership = buildOwnership(Set.of(USER_CREATOR, USER_OWNER), Set.of(), USER_CREATOR);

        var fileMetadata = FileMetadata.builder()
                .id(id)
                .fileId(fileId)
                .ownershipDetails(ownership)
                .createdBy(USER_CREATOR)
                .build();

        when(storageService.deleteFile(anyString())).thenReturn(true);
        when(fileRepository.findByFileId(fileId)).thenReturn(Optional.of(fileMetadata));

        assertDoesNotThrow(() -> {
            fileService.deleteFile(fileId, USER_OWNER);
        }, "The deleteFile method should not throw any exception");

        verify(fileRepository).deleteById(id);
    }

    @Test
    void deleteFile_DoThrow() {
        String fileId = "test-file-id";
        String requesterEmail = "authorized@example.com";

        var ownership = buildOwnership(Set.of(USER_CREATOR, USER_OWNER), Set.of(requesterEmail), USER_CREATOR);

        var fileMetadata = FileMetadata.builder()
                .fileId(fileId)
                .ownershipDetails(ownership)
                .createdBy(USER_CREATOR)
                .build();

        when(fileRepository.findByFileId(fileId))
                .thenReturn(Optional.of(fileMetadata));
        doThrow(new RuntimeException("Failed to delete file"))
                .when(storageService).deleteFile(fileId);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> fileService.deleteFile(fileId, requesterEmail));
        assertEquals("Failed to delete file", exception.getMessage());

        // Verify that the repository delete method was not called due to the exception
        verify(fileRepository, never()).deleteById(fileId);
    }

    @Test
    void deleteFile_DoAnswer_doThrow() {
        String fileId = "file-id-123";
        // String fileId2 = "file-id-1234";

        var ownership = buildOwnership(Set.of(USER_CREATOR, USER_OWNER), Set.of(USER_CREATOR), USER_CREATOR);

        var fileMetadata = FileMetadata.builder()
                .fileId(fileId)
                .ownershipDetails(ownership)
                .createdBy(USER_CREATOR)
                .build();

        when(fileRepository.findByFileId(fileId)).thenReturn(Optional.of(fileMetadata));

        doAnswer(invocation -> {
            String id = invocation.getArgument(0);
            if (id.equals(fileId)) {
                return false;
            }
            return true;
        }).when(storageService).deleteFile(anyString());

        FileException exception = assertThrows(FileException.class,
                () -> fileService.deleteFile(fileId, USER_CREATOR));
        assertEquals("Failed to delete file", exception.getMessage());
    }

    // TODO - Update DELETE to work as parametrized test

    // TODO - PATCH

    // TODO - Write parametrized tests for retrieveFileMetadata

}