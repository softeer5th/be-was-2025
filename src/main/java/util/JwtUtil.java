package util;

import config.Configuration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import model.User;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final int EXPIRATION_TIME = 3600000; // 60 * 60 * 1000
    private static final String secretKey = Configuration.getSecretKey();

    private JwtUtil() {}

    public static String generateToken(User user) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String userId = user.getUserId();
        String name = user.getName();
        if (userId == null) return null;

        return Jwts.builder()
                .subject(userId)
                .claim("name", name)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(key)
                .compact();
    }
}
