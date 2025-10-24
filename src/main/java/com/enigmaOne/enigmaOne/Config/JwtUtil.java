package com.enigmaOne.enigmaOne.Config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Component
public class JwtUtil {


    private  String secret_token="lkashdkaskdaskjdhaskjsdhk";
    private  final Algorithm ALGORITHM = Algorithm.HMAC256(secret_token);


    public String generateJWT(String email){
        return JWT.create()
                .withSubject(email)
                .withIssuer("auth0")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))
                .sign(ALGORITHM);
    }
    public boolean isValidToken(String jwt)  {
        try{
            JWT.require(ALGORITHM)
                    .build()
                    .verify(jwt);
            return true;
        }catch (JWTVerificationException e){
            return false;

        }
    }
    public String getUsername(String jwt) {
        return JWT.require(ALGORITHM)
                .build()
                .verify(jwt)
                .getSubject();
    }



}
