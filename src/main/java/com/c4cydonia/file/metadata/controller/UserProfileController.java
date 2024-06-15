package com.c4cydonia.file.metadata.controller;

import com.c4cydonia.file.metadata.model.UserProfile;
import com.c4cydonia.file.metadata.service.IUserProfileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/profiles")
public class UserProfileController {

    private final IUserProfileService userProfileService;

    public UserProfileController(IUserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public UserProfile createProfile(@RequestBody UserProfile profile) {
        return userProfileService.createProfile(profile);
    }

    @GetMapping
    public List<UserProfile> getAllProfiles() {
        return userProfileService.getAllProfiles();
    }

    @GetMapping("/{id}")
    public UserProfile getProfileById(@PathVariable String id) {
        return userProfileService.getProfileById(id);
    }

    @PutMapping("/{id}")
    public UserProfile updateProfile(@PathVariable String id, @RequestBody UserProfile updatedProfile) {
        return userProfileService.updateProfile(id, updatedProfile);
    }

    @DeleteMapping("/{id}")
    public void deleteProfile(@PathVariable String id) {
        userProfileService.deleteProfile(id);
    }
}
