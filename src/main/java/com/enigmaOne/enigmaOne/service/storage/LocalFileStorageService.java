package com.enigmaOne.enigmaOne.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.storage.local.path:uploads}")
    private String uploadDir;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) return null;
        try {
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }
            String fileName = UUID.randomUUID().toString() + ext;
            Path dirPath = Paths.get(uploadDir, folder);
            Files.createDirectories(dirPath);
            Path filePath = dirPath.resolve(fileName);
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(file.getBytes());
            }
            // devolver la ruta absoluta como URL de acceso local
            return filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo localmente: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return false;
        try {
            Path p = Paths.get(fileUrl);
            return Files.deleteIfExists(p);
        } catch (Exception e) {
            return false;
        }
    }
}

