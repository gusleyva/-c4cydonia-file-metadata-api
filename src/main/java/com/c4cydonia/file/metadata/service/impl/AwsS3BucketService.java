package com.c4cydonia.file.metadata.service.impl;

// import com.amazonaws.services.s3.AmazonS3;
// import com.amazonaws.services.s3.model.ObjectMetadata;
// import com.amazonaws.services.s3.model.S3Object;

import com.c4cydonia.file.metadata.service.IStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class AwsS3BucketService implements IStorageService {

    // @Autowired
    // private AmazonS3 amazonS3;
    private String bucketName = "your-bucket-name";

    @Override
    public String constructFileUrl(String uuid, String originalFileName) {
        // Use a date to partition the file path in the bucket, for example:
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String datePath = LocalDate.now().format(formatter);

        // Construct the full S3 key with the date path, UUID, and the original filename
        return datePath + "/" + uuid + "-" + originalFileName;
    }

    @Override
    public String saveFile(String fileId, MultipartFile file) {
        String path = "files/" + fileId;
            /*
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucketName, path, file.getInputStream(), metadata);
            return amazonS3.getUrl(bucketName, path).toString();
             */
        return "s3://" + bucketName + "/" + path;
    }

    @Override
    public byte[] downloadFile(String fileId) {
        String path = "files/" + fileId;
        /*
        S3Object s3Object = amazonS3.getObject(bucketName, path);
        try {
            return s3Object.getObjectContent().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file from S3", e);
        }
         */
        return new byte[0];
    }

    @Override
    public void updateFile(String fileId, MultipartFile file) {
        // Overwriting the existing file
        saveFile(fileId, file);
    }

    /**
     * Delete could be async
     * - If something fails we could retry delete
     * - Also get a list of deleted files
     * @param fileId
     */
    @Override
    public boolean deleteFile(String fileId) {
            /*
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucketName, path, file.getInputStream(), metadata);
            return amazonS3.getUrl(bucketName, path).toString();
             */
        String path = "s3://" + bucketName + "/files/" + fileId;

        // amazonS3.deleteObject(bucketName, path);
        return true;
    }
}
