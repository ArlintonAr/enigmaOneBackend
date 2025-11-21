package com.enigmaOne.enigmaOne.service.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.enigmaOne.enigmaOne.service.FileSizeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryFileStorageService implements FileStorageService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private FileSizeValidator fileSizeValidator;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
            if (file == null || file.isEmpty()) return null;
            // validar tamaño
            this.fileSizeValidator.validate(file);

            String publicId = UUID.randomUUID().toString();
            Map uploadResult = this.cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", folder + "/",
                            "resource_type", "raw" // para PDF y otros archivos
                    ));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Error al subir archivo a Cloudinary: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return false;
        String publicId = extractPublicIdFromUrl(fileUrl);
        if (publicId == null) return false;
        try {
            this.cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String extractPublicIdFromUrl(String url) {
        String[] parts = url.split("/");
        if (parts.length > 2) {
            String lastPart = parts[parts.length - 1];
            String folderPart = parts[parts.length - 2];

            String[] splitLastPart = lastPart.split("\\.");
            String uuid = splitLastPart[0];

            return folderPart + "/" + uuid;
        }
        return null;
    }
}

