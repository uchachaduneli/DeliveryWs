package ge.bestline.delivery.ws.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonIgnore
    private Integer deleted;
    private String name;
    @Transient
    private String url;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Parcel parcel;
    @ManyToOne(cascade = CascadeType.DETACH)
    private User author;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdTime;

    public Files(String name, Parcel parcel, User author) {
        this.name = name;
        this.parcel = parcel;
        this.author = author;
    }

    public Files(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @PrePersist
    protected void onCreate() {
        deleted = 2;
        createdTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = new Date();
    }
}
