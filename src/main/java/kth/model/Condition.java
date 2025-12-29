package kth.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conditions")
public class Condition extends PanacheEntity {
    @Column(nullable = false)
    public String diagnosis;

    public String description;


    @Column(name = "diagnosis_date")
    public LocalDateTime diagnosisDate;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    public Patient patient;

    @ManyToOne
    @JoinColumn(name = "diagnosed_by")
    public Practitioner diagnosedBy;

    @Enumerated(EnumType.STRING)
    public ConditionStatus status;

}
