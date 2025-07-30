package io.mhetko.lor.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Klucz tajny pobierany z pliku konfiguracyjnego
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Czas ważności tokena (1 godzina)
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    // Tworzy obiekt SecretKey na podstawie klucza tekstowego
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generuje token JWT na podstawie nazwy użytkownika.
     *
     * @param username nazwa użytkownika
     * @return token JWT jako String
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // ustawia subject (np. login)
                .setIssuedAt(new Date()) // data wydania tokena
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // data wygaśnięcia
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // podpisuje token kluczem i algorytmem
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
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // ustawia klucz do weryfikacji podpisu
                .build()
                .parseClaimsJws(token) // parsuje token
                .getBody(); // zwraca claims
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