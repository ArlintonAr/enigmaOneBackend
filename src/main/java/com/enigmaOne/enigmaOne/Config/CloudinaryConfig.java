package com.enigmaOne.enigmaOne.Config;


import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class CloudinaryConfig {

    @Value("${CLOUDE_NAME}")
    private String cloudeName;
    @Value(("${API_KEY_CLOUDINARY}"))
    private String apiKey;
    @Value("${API_SECRET_CLOUDINARY}")
    private String apiSecret;


    @Bean
    public Cloudinary cloudinary(){
        Map<String, String> config =new HashMap<>();
        config.put("cloud_name",this.cloudeName);
        config.put("api_key",this.apiKey);
        config.put("api_secret",this.apiSecret);

        System.out.println(this.cloudeName);
        return  new Cloudinary(config);
    }
}
