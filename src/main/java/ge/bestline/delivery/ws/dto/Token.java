package ge.bestline.delivery.ws.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class Token {
    public LocalDateTime created;
    public TokenUser user;
    public String token;
    @Value("${jwt.token.expired}")
    public String expiredInSecs;

    public Token(LocalDateTime created, TokenUser user, String token) {
        this.created = created;
        this.user = user;
        this.token = token;
    }
}

