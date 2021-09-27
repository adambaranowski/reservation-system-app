package pl.adambaranowski.rsbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import pl.adambaranowski.rsbackend.model.Authority;
import pl.adambaranowski.rsbackend.model.User;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final SecretKey KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final JwtParser TOKEN_PARSER = Jwts.parserBuilder().setSigningKey(KEY).build();
    private final int EXPIRATION_TIME_SECONDS;

    @Autowired
    public JwtService(@Value("${jwt.expiration.seconds:180}") Integer expirationTime) {
        this.EXPIRATION_TIME_SECONDS = expirationTime;
    }

    public String generateTokenForUser(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                //.setClaims(prepareClaims(user))
                .setExpiration(calculateExpirationDate())
                .signWith(KEY)
                .compact();
    }

    private Map<String, String> prepareClaims(User user){
        Map<String, String> claims = new HashMap<>();

        List<String> authorities = user.getAuthorities().stream().map(Authority::getRole).collect(Collectors.toList());

        claims.put("subject", user.getEmail());
        claims.put("authorities", authorities.toString());
        claims.put("nick", user.getUserNick());

        return claims;
    }

    public String extractSubjectIfTokenIsValid(String jwtToken) {
        try {
            //throws exception if token is wrong
            Claims claims = TOKEN_PARSER.parseClaimsJws(jwtToken).getBody();

            return claims.getSubject();
        } catch (Exception e) {
            throw new AuthenticationException("Authentication denied: " + e.getMessage()) {
            };
        }
    }

    private Date calculateExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRATION_TIME_SECONDS * 1000L);
    }
}
