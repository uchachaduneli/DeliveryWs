package ge.bestline.delivery.ws.security.jwt;

import ge.bestline.delivery.ws.dto.TokenUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    public String GenerateToken(TokenUser user) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, user);
    }


    private String createToken(Map<String, Object> claims, TokenUser user) {
        claims.put("user", user);
        claims.put("roles", user.getRole().stream().map((r) -> r.getName()).collect(Collectors.toList()));
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUserName())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public TokenUser getRequesterUserData(HttpServletRequest req) {
        String token = resolveToken(req);
        Claims allClaims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        LinkedHashMap m = (LinkedHashMap) allClaims.get("user");
        TokenUser user = new TokenUser();
        user.setId((Integer) m.get("id"));
        user.setUserName((String) m.get("userName"));
        user.setName((String) m.get("name"));
        user.setLastName((String) m.get("lastName"));
        if (m.get("warehouseId") != null) {
            user.setWarehouseId((Integer) m.get("warehouseId"));
        }
        user.setRole(new HashSet((Collection) allClaims.get("roles")));
        user.setFromGlobalSite(req.getHeader("GL") != null);
        return user;
    }
}
