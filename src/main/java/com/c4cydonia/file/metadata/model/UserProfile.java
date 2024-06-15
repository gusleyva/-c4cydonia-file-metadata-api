package com.c4cydonia.file.metadata.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "userProfiles")
public class UserProfile {
    @Id
    private String id;
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private Date birthday;
    private int age;
    private String genderIdentity;
    private String sexualOrientation;
    private String location; // Simplified as a String just for POC
    private List<String> photos; // List of photo URLs
    private List<String> interests;
    private String education; // Simplified as a String
    private String employment; // Simplified as a String
    private String religion;
    private String ethnicity;
    private String relationshipGoals;
    private String lifestyleInformation; // Simplified as a String
    private String activityStatus;
    private Date lastOnline;
    private Date created_at;
    private Date updated_at;
}
