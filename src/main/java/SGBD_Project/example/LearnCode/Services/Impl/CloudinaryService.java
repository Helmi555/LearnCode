package SGBD_Project.example.LearnCode.Services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFileToFolder(MultipartFile file, String folderPath) throws IOException {
        // Get the original file name and type (extension)
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new IOException("File has no extension");
        }

        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

        // Add timestamp to make the file name unique
        String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS").format(new Date());
        String publicId = folderPath + "/" + originalFileName.substring(0, originalFileName.lastIndexOf('.')) + "_" + timestamp;

        // Determine if the file is an image or a video based on the extension
        String resourceType = "image"; // Default to image
        if (isVideoFile(extension)) {
            resourceType = "video";
        }

        // Set the public ID and resource type (dynamic based on the file type)
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folderPath,
                "public_id", publicId,
                "resource_type", resourceType
        );

        // Upload the file to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

        // Return the URL of the uploaded file (image or video)
        return uploadResult.get("url").toString();
    }

    // Helper method to check if the file extension corresponds to a video
    private boolean isVideoFile(String extension) {
        String[] videoExtensions = {"mp4", "avi", "mov", "flv", "mkv", "wmv"};
        for (String ext : videoExtensions) {
            if (ext.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    public String deleteFile(String publicId) throws IOException {
        Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        return deleteResult.get("result").toString(); // "ok" indicates a successful deletion
    }


}
