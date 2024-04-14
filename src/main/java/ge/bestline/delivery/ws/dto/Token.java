package ge.bestline.delivery.ws.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

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

