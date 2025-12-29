// file: src/main/java/kth/model/Practitioner.java
package kth.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "practitioners")
public class Practitioner extends PanacheEntity {

    @Column(name = "first_name")
    public String firstName;

    @Column(name = "last_name")
    public String lastName;

    @Column(name = "license_number")
    public String licenseNumber;

    @Column(name = "user_id")
    public String userId;

    @OneToMany(mappedBy = "practitioner")
    public List<Encounter> encounters;

    @Enumerated(EnumType.STRING)
    public PractitionerType type;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    public Organization organization;
}
