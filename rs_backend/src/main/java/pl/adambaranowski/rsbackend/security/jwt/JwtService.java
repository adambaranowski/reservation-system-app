package pl.adambaranowski.rsbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class JwtService {
    private static final String PUBLIC_KEY_URL = "http://localhost:8081/publicKey";

    private final RestTemplate restTemplate = new RestTemplate();
    private JwtParser tokenParser;

////    @Value("${spring.profiles.active}")
////    private String activeProfile;
//
//    @Autowired
//    private Environment environment;

    public JwtService() {
    }

    @PostConstruct
    private void getKey() {

            String key = restTemplate.getForEntity(PUBLIC_KEY_URL, PublicKeyDto.class).getBody().getKey();

            byte[] byteKey = Base64.getDecoder().decode(key.getBytes());
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);

            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                PublicKey publicKey = kf.generatePublic(X509publicKey);
                this.tokenParser = Jwts.parserBuilder().setSigningKey(publicKey).build();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

//    public String generateTokenForUser(User user) {
//        return Jwts.builder()
//                .setSubject(user.getEmail())
//                //.setClaims(prepareClaims(user))
//                .setExpiration(calculateExpirationDate())
//                .signWith(KEYS.getPrivate())
//                .compact();
//    }
//
//    private Map<String, String> prepareClaims(User user){
//        Map<String, String> claims = new HashMap<>();
//
//        List<String> authorities = user.getAuthorities().stream().map(Authority::getRole).collect(Collectors.toList());
//
//        claims.put("subject", user.getEmail());
//        claims.put("authorities", authorities.toString());
//        claims.put("nick", user.getUserNick());
//
//        return claims;
//    }

    public String extractSubjectIfTokenIsValid(String jwtToken) {
        try {
            //throws exception if token is wrong
            Claims claims = tokenParser.parseClaimsJws(jwtToken).getBody();

            return claims.getSubject();
        } catch (Exception e) {
            throw new AuthenticationException("Authentication denied: " + e.getMessage()) {
            };
        }
    }

//    private Date calculateExpirationDate() {
//        return new Date(System.currentTimeMillis() + EXPIRATION_TIME_SECONDS * 1000L);
//    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublicKeyDto {
        private String key;
    }
}
