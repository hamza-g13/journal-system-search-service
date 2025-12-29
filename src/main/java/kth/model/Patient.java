// file: src/main/java/kth/model/Patient.java
package kth.model;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient extends PanacheEntity {

    @Column(name = "first_name")
    public String firstName;

    @Column(name = "last_name")
    public String lastName;

    @Column(name = "social_security_number")
    public String socialSecurityNumber;

    @Column(name = "date_of_birth")
    public LocalDate dateOfBirth;

    @OneToMany(mappedBy = "patient")
    public List<Condition> conditions;

    @OneToMany(mappedBy = "patient")
    public List<Encounter> encounters;
}
