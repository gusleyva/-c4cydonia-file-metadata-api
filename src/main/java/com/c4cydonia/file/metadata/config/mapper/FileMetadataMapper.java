package com.c4cydonia.file.metadata.config.mapper;

import com.c4cydonia.file.metadata.model.FileMetadata;
import com.c4cydonia.file.metadata.model.FileMetadataRequestDto;
import com.c4cydonia.file.metadata.model.OwnershipDetails;
import com.c4cydonia.file.metadata.model.OwnershipRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileMetadataMapper {

    FileMetadataMapper INSTANCE = Mappers.getMapper(FileMetadataMapper.class);

    @Mapping(target = "ownershipDetails", ignore = true)
    void updateFileMetadataFromDto(FileMetadataRequestDto dto, @MappingTarget FileMetadata entity);

    default void updateOwnershipDetails(OwnershipRequestDto dto, @MappingTarget OwnershipDetails entity) {
        if (dto.getOwners() != null && !dto.getOwners().isEmpty()) {
            entity.setOwners(dto.getOwners());
        }
        if (dto.getReceivers() != null) {
            entity.setReceivers(dto.getReceivers());
        }
    }
}
