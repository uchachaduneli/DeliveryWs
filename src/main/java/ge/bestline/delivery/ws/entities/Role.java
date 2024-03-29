package ge.bestline.delivery.ws.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role {
    @Id
    private String name;
}
