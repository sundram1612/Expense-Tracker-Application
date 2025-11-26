package com.sundramproject.ExpenseTracker_backend.Security;

import com.sundramproject.ExpenseTracker_backend.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey(){
        if(jwtSecretKey == null || jwtSecretKey.trim().isEmpty()){
            throw new IllegalStateException("Jwt Secret Key is not configured.");
        }
        if(jwtSecretKey.length() < 32){
            throw new IllegalStateException("Jwt Secret key must be at least 32 characters long. Current length is "+ jwtSecretKey.length());
        }
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiry = new Date(nowMillis + 1000 * 60 * 60 * 24 * 7);

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSecretKey())
                .compact();
    }


    public String getEmailFromToken(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        }
        catch(Exception e){
            return false;
        }
    }

}
