package ge.bestline.delivery.ws.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Packages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double length;
    private Double width;
    private Double height;
    private Double volumeWeight;
    private String plombNumber;
    private String boxNumber;
    @ManyToOne(cascade = CascadeType.DETACH)
    private Parcel parcel;
}
