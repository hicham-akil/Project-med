package org.example.backend_med.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "${}";
    private final long EXPIRATION_MS= 1000*60*60;

    public String GenerateJwtToken(String name, String role,Long user_id){
       return Jwts.builder()
               .setSubject(name)
               .claim("role",role)
               .claim("user_id",user_id)
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
               .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
               .compact();
    }
    public boolean validateJwtToken(String token){
        try {
             Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
             return true;
        }catch (Exception e){
            return false;
        }
    }
    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY)
                .parseClaimsJws(token).getBody();
    }

}
