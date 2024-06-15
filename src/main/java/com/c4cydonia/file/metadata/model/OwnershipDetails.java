package com.c4cydonia.file.metadata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OwnershipDetails {
    private Set<String> owners;
    private Set<String> receivers;
    private String addedBy;
}
