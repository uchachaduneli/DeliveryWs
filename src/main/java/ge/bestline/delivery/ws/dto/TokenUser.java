package ge.bestline.delivery.ws.dto;

import ge.bestline.delivery.ws.entities.Role;
import ge.bestline.delivery.ws.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class TokenUser {
    private String name;
    private String lastName;
    private String userName;
    private Set<Role> role;

    public TokenUser(User u) {
        this.name = u.getName();
        this.lastName = u.getLastName();
        this.userName = u.getUserName();
        this.role = u.getRole();
    }
}
