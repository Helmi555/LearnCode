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
        // Get original file name without the extension
        String originalFileName = file.getOriginalFilename();
        if (originalFileName != null && originalFileName.contains(".")) {
            originalFileName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        }

        // Add timestamp to make the file name unique
        String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS").format(new Date());
        String publicId = folderPath + "/" + originalFileName + "_" + timestamp;

        // Set the public ID in the upload options
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folderPath,
                "public_id", publicId
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return uploadResult.get("url").toString(); // Returns the URL of the uploaded image
    }

    public String deleteFile(String publicId) throws IOException {
        Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        return deleteResult.get("result").toString(); // "ok" indicates a successful deletion
    }


}
