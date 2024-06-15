package com.c4cydonia.file.metadata.utils;

import com.c4cydonia.file.metadata.model.FileMetadata;
import com.c4cydonia.file.metadata.model.OwnershipDetails;
import com.c4cydonia.file.metadata.repository.FileMetadataRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(FileMetadataRepository fileRepository) {
        return args -> {
            OwnershipDetails ownershipDetails = OwnershipDetails.builder()
                    .owners(Set.of("example@example.com"))
                    .receivers(Set.of("other@example.com"))
                    .addedBy("creator@example.com")
                    .build();
            FileMetadata fileMetadata = FileMetadata.builder()
                    .fileId("123456")
                    .fileName("example.pdf")
                    .fileSize(123456L)
                    .contentType("application/pdf")
                    .createdBy("creator@example.com")
                    .text("Sample text")
                    .title("Sample Title")
                    .fileUrl("files/example.pdf")
                    .ownershipDetails(ownershipDetails)
                    .build();
            fileRepository.save(fileMetadata);
        };
    }
}
