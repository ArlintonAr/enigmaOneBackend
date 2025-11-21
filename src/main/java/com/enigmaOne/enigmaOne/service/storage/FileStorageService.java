package com.enigmaOne.enigmaOne.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Subir un archivo y devolver la URL accesible/public_id según la implementación
     */
    String uploadFile(MultipartFile file, String folder);

    /**
     * Borrar un archivo a partir de la URL/identificador almacenado
     */
    boolean deleteFile(String fileUrl);
}

