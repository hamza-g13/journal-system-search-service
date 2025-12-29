// file: src/main/java/kth/model/Encounter.java
package kth.model;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "encounters")
public class Encounter extends PanacheEntity {

    @Column(name = "encounter_date")
    public LocalDateTime encounterDate;

    public String notes;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    public Patient patient;

    @ManyToOne
    @JoinColumn(name = "practitioner_id")
    public Practitioner practitioner;
}
