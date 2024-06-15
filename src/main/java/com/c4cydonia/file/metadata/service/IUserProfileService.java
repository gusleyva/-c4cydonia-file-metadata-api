package com.c4cydonia.file.metadata.service;


import com.c4cydonia.file.metadata.model.UserProfile;

import java.util.List;

public interface IUserProfileService {
    UserProfile createProfile(UserProfile profile);
    List<UserProfile> getAllProfiles();
    UserProfile getProfileById(String id);
    UserProfile updateProfile(String id, UserProfile updatedProfile);
    void deleteProfile(String id);
}