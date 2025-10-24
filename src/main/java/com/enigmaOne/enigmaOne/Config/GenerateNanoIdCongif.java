package com.enigmaOne.enigmaOne.Config;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GenerateNanoIdCongif {

    private final char[] alphabet = {'0','1','2','3','4','5','6','7','8','9'};

    private final Random random = new Random();

    public String generateNanoId(){
        return NanoIdUtils.randomNanoId(random,alphabet,10);
    }

}
