package it.unisannio.studenti.p.perugini.pps_compiler.Components;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.jwtKey}")
    private String key;
    private Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    public String generateJWT(User user){
        logger.info("ecco la chiave di criptazione: "+this.key+", procedo alla creazione del token");
        SecretKey secretKey = new SecretKeySpec(this.key.getBytes(),"HmacSHA256");
        return Jwts.builder()
                .setSubject(user.getEmail().getEmail())
                .setExpiration(Date.from(Instant.now().plus(8, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

    public String getSubjectFromToken(String jwt){
        logger.info("ecco il token ricevuto: "+jwt);
        logger.info("ecco la chiave di criptazioen: "+this.key);
        SecretKey secretKey = new SecretKeySpec(this.key.getBytes(),"HmacSHA256");
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    public Boolean isExpired(String jwt){
        SecretKey secretKey = new SecretKeySpec(this.key.getBytes(),"HmacSHA256");
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);
            return false;
        }catch (ExpiredJwtException e){
            logger.info("TOKEN scaduto");
            return true;
        }

    }
}
