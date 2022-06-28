package ge.bestline.delivery.ws.security.jwt;

import ge.bestline.delivery.ws.dto.TokenUser;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(TokenUser user) {
        Claims claims = Jwts.claims().setSubject(user.getName() + "  " + user.getLastName());
        claims.put("user", user);
        claims.put("roles", user.getRole().stream().map((r) -> r.getName()).collect(Collectors.toList()));
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder().setClaims(claims)
                .setIssuedAt(now).setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = getUserDetailsFromToken(token);
        log.info("Authentication - UserDetails: {} token {}", userDetails, token);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private UserDetails getUserDetailsFromToken(String token) {
        Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

        List<GrantedAuthority> authorities = new ArrayList<>();

        List<String> roles = (ArrayList) body.get("roles");
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return new org.springframework.security.core.userdetails.User(body.getSubject(), "", authorities);
    }

    public String getClaim(String token, String claim) {
        return (String) Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().get(claim);
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }
}
