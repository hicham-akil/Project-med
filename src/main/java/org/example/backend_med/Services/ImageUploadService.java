package org.example.backend_med.Services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size exceeds 5MB limit");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "profile_pictures",
                            "resource_type", "image",
                            "transformation", new Transformation().width(500).height(500).crop("limit")
                    )
            );

            String secureUrl = uploadResult.get("secure_url").toString();
            System.out.println("Image uploaded successfully to Cloudinary: " + secureUrl);
            return secureUrl;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }


            public void deleteImage(String imageUrl) {
        try {
            // Extract public_id from URL
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            System.out.println("Image deleted from Cloudinary: " + publicId);
        } catch (IOException e) {
            System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    private String extractPublicId(String imageUrl) {
        // Extract public_id from Cloudinary URL
        // Example: https://res.cloudinary.com/demo/image/upload/v1234/profile_pictures/abc123.jpg
        // Returns: profile_pictures/abc123
        String[] parts = imageUrl.split("/upload/");
        if (parts.length > 1) {
            String[] pathParts = parts[1].split("/");
            // Skip version number if present
            int startIndex = pathParts[0].startsWith("v") ? 1 : 0;
            String publicId = String.join("/", java.util.Arrays.copyOfRange(pathParts, startIndex, pathParts.length));
            // Remove file extension
            return publicId.substring(0, publicId.lastIndexOf('.'));
        }
        return "";
    }
}