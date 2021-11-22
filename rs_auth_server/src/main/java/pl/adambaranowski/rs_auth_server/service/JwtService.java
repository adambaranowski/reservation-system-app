package pl.adambaranowski.rs_auth_server.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.adambaranowski.rs_auth_server.model.User;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Base64;
import java.util.Date;


@Service
public class JwtService {
    private static final KeyPair KEYS = Keys.keyPairFor(SignatureAlgorithm.RS256);
    private final int EXPIRATION_TIME_SECONDS;

    @Autowired
    public JwtService(@Value("${jwt.expiration.seconds:180}") Integer expirationTime) {
        this.EXPIRATION_TIME_SECONDS = expirationTime;
    }

    public String generateTokenForUser(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(calculateExpirationDate())
                .signWith(KEYS.getPrivate())
                .compact();
    }

    public String getPublicKey() {
        return new String(Base64.getEncoder().encode(KEYS.getPublic().getEncoded()), StandardCharsets.UTF_8);
    }

    private Date calculateExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRATION_TIME_SECONDS * 1000L);

    }
}
