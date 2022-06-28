package ge.bestline.delivery.ws.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class Token {
    public TokenUser user;
    public String token;

    public Token(TokenUser user, String token) {
        this.user = user;
        this.token = token;
    }
}

