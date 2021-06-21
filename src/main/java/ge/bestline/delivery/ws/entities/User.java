package ge.bestline.delivery.ws.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String lastName;
    private String phone;
    private String personalNumber;
    @OneToOne(fetch = FetchType.LAZY)
    private City city;
    @OneToOne(fetch = FetchType.LAZY)
    private Route route;
    private String role;
    @OneToOne(fetch = FetchType.LAZY)
    private UserStatus status;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = new UserStatus(1);
        }
    }
}
