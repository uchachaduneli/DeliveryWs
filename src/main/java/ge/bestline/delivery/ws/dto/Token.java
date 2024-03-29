package ge.bestline.delivery.ws.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class Token {
    public TokenUser user;
    public String token;

    public Token(String token, TokenUser user) {
        this.token = token;
        this.user = user;
    }
}

