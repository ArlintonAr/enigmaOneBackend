package com.enigmaOne.enigmaOne.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadPhotoToCloudinary (MultipartFile file,String folderName) {

        try {

            String publicId = UUID.randomUUID().toString();
            Map uploadResult =this.cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id",publicId,
                            "folder",folderName + "/", //organiza la carpeta
                            "resource_type","image" //asegurarnos que sube imagen
                            //No sobrescribir si ya existe, pendiente

                    ));
            return (String) uploadResult.get("secure_url"); //obtenemos la url para almacenar en nuestra base de datos
        } catch (Exception e) {
            throw new RuntimeException("Ha ocurrido un problema al subir la foto a Cloudinary: "+e );
        }
    }

    public boolean deletePhotoOnCloudinary ( String photoUrl) throws IOException{

        if (photoUrl ==null || photoUrl.isEmpty()){
            return false;
        }
        String publicId = extractPublicIdFromUrl(photoUrl);

        if (publicId != null){
            this.cloudinary.uploader().destroy(publicId,ObjectUtils.emptyMap());
            return  true;
        }
        return  false;
    }

    private String extractPublicIdFromUrl(String  url){
        String[] parts = url.split("/");
        if (parts.length>2){
            String lastPart = parts[parts.length-1];
            String folderPart = parts[parts.length-2];

            String[] splitLastPart = lastPart.split("\\.");
            String uuidEmployee = splitLastPart[0];

            String publicId = folderPart+"/"+uuidEmployee;
            return publicId;
        }
        return null;
    }
}
