package com.enigmaOne.enigmaOne.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileSizeValidator {

    /**
     * Tamaño máximo en bytes para fotos. Valor por defecto 5MB si no se configura.
     * Puedes sobrescribir con la property: app.photo.max-size-bytes
     */
    @Value("${app.photo.max-size-bytes:5242880}")
    private long maxSizeBytes;

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return; // no hay archivo, nada que validar aquí
        }
        long size = file.getSize();
        if (size > maxSizeBytes) {
            throw new IllegalArgumentException("El archivo supera el tamaño máximo permitido de " + maxSizeBytes + " bytes");
        }
    }

    // helper para tests/uso programático
    public long getMaxSizeBytes() {
        return maxSizeBytes;
    }
}

