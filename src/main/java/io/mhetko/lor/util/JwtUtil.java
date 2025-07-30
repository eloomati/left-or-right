package io.mhetko.lor.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    /**
     * Generuje token JWT na podstawie nazwy użytkownika.
     *
     * @param username nazwa użytkownika
     * @return token JWT jako String
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Wyciąga nazwę użytkownika z tokenu JWT.
     *
     * @param token token JWT
     * @return nazwa użytkownika
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Sprawdza, czy token jest poprawny i należy do danego użytkownika.
     *
     * @param token    token JWT
     * @param username nazwa użytkownika
     * @return true jeśli token jest poprawny i nie wygasł
     */
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Pobiera wszystkie claims (dane) z tokenu JWT.
     *
     * @param token token JWT
     * @return claims
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Sprawdza, czy token JWT wygasł.
     *
     * @param token token JWT
     * @return true jeśli token wygasł
     */
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
