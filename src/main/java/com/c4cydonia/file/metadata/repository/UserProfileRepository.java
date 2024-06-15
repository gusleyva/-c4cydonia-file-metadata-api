package com.c4cydonia.file.metadata.repository;

import com.c4cydonia.file.metadata.model.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
}