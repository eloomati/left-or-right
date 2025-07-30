package io.mhetko.lor.filter;

import io.mhetko.lor.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. Pobierz nagłówek Authorization z żądania
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Sprawdź czy nagłówek istnieje i zaczyna się od "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // 3. Wyodrębnij token JWT
            username = jwtUtil.extractUsername(token); // 4. Wyciągnij nazwę użytkownika z tokenu
        }

        // 5. Jeśli użytkownik nie jest jeszcze uwierzytelniony i token jest poprawny
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(token, username)) {
                // 6. Utwórz obiekt User (Spring Security) z pustą listą ról
                User userDetails = new User(username, "", Collections.emptyList());
                // 7. Utwórz token uwierzytelniający
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                // 8. Ustaw dodatkowe szczegóły uwierzytelnienia
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 9. Ustaw uwierzytelnienie w kontekście bezpieczeństwa
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // 10. Przekaż żądanie dalej w łańcuchu filtrów
        filterChain.doFilter(request, response);
    }
}