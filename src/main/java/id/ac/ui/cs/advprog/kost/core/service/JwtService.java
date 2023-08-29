package id.ac.ui.cs.advprog.kost.core.service;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import java.security.Key;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY = "645367566B59703373367639792F423F4528482B4D6251655468576D5A713474";

    // ROLE: PENGELOLA & PELANGGAN

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Integer extractUserId(String token) {
        final var claims = extractAllClaims(token);
        return (Integer) claims.get("id");
    }

    public String extractUserRole(String token) {
        final var claims = extractAllClaims(token);
        return claims.get("role").toString();
    }

    public Object extractValue(String token, String key) {
        final var claims = extractAllClaims(token);
        return claims.get(key);
    }

    public boolean isTokenValid(String token) {
        return isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).after(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final var claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
