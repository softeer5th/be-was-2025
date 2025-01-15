package util;

import config.Configuration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import model.User;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String secretKey = Configuration.getSecretKey();

    public static String generateToken(User user) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String userId = user.getUserId();
        if (userId == null) return null;

        return Jwts.builder()
                .subject(userId)
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(key)
                .compact();
    }
}
