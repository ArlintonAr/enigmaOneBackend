package com.enigmaOne.enigmaOne.Config;

import com.enigmaOne.enigmaOne.service.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileStorageManager {

    @Value("${app.storage.provider:cloudinary}")
    private String provider; // cloudinary | local

    @Autowired
    private ApplicationContext applicationContext;

    public FileStorageService getActiveService() {
        String p = provider == null ? "cloudinary" : provider.toLowerCase();
        try {
            if (p.equals("local")) {
                return applicationContext.getBean("localFileStorageService", FileStorageService.class);
            }
            // default cloudinary
            return applicationContext.getBean("cloudinaryFileStorageService", FileStorageService.class);
        } catch (Exception e) {
            // fallback to cloudinary bean if anything falla
            return applicationContext.getBean("cloudinaryFileStorageService", FileStorageService.class);
        }
    }
}

