package kth.resource;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import kth.dto.EncounterDTO;
import kth.dto.PatientDTO;
import kth.dto.PractitionerDTO;
import kth.model.Condition;
import kth.model.Encounter;
import kth.model.Patient;
import kth.model.Practitioner;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Authenticated
public class SearchResource {

        @Inject
        JsonWebToken jwt;

        @GET
        @Path("/patients")
        @RolesAllowed({ "DOCTOR", "NURSE", "ADMIN" })
        public Uni<List<PatientDTO>> searchPatients(
                        @QueryParam("q") String query,
                        @QueryParam("condition") String diagnosis) {

                System.out.println("Search request from: " + jwt.getName() + " with role: " + jwt.getGroups());

                String hqlBuilder = "SELECT DISTINCT p FROM Patient p " +
                                "LEFT JOIN FETCH p.conditions c " +
                                "WHERE 1=1 ";

                if (query != null && !query.isBlank()) {
                        hqlBuilder += "AND (LOWER(p.firstName) LIKE LOWER('%" + query
                                        + "%') OR LOWER(p.lastName) LIKE LOWER('%"
                                        + query + "%')) ";
                }

                if (diagnosis != null && !diagnosis.isBlank()) {
                        hqlBuilder += "AND LOWER(c.diagnosis) LIKE LOWER('%" + diagnosis + "%') ";
                }

                final String finalHql = hqlBuilder;

                return Panache.getSession()
                                .chain(session -> session.createQuery(finalHql, Patient.class).getResultList())
                                .map(patients -> patients.stream()
                                                .map(this::toPatientDTO)
                                                .collect(Collectors.toList()));
        }

        @GET
        @Path("/practitioners")
        @RolesAllowed({ "DOCTOR", "NURSE", "ADMIN" })
        public Uni<List<PractitionerDTO>> searchPractitioners(@QueryParam("q") String query) {
                if (query == null || query.isBlank()) {
                        return Practitioner.<Practitioner>findAll().list()
                                        .map(practitioners -> practitioners.stream()
                                                        .map(this::toPractitionerDTO)
                                                        .collect(Collectors.toList()));
                }

                String hql = "SELECT p FROM Practitioner p WHERE " +
                                "LOWER(p.firstName) LIKE LOWER(:query) OR " +
                                "LOWER(p.lastName) LIKE LOWER(:query) OR " +
                                "LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE LOWER(:query)";

                return Panache.getSession()
                                .chain(session -> session.createQuery(hql, Practitioner.class)
                                                .setParameter("query", "%" + query + "%")
                                                .getResultList())
                                .map(practitioners -> practitioners.stream()
                                                .map(this::toPractitionerDTO)
                                                .collect(Collectors.toList()));
        }

        @GET
        @Path("/doctor/{practitionerId}/patients")
        public Uni<List<PatientDTO>> getDoctorPatients(@PathParam("practitionerId") Long practitionerId) {

                return Panache.getSession().chain(session -> {
                        return session.createQuery(
                                        "SELECT e.patient FROM Encounter e WHERE e.practitioner.id = :id",
                                        Patient.class)
                                        .setParameter("id", practitionerId)
                                        .getResultList()
                                        .chain(patientsFromEncounters -> {

                                                return session.createQuery(
                                                                "SELECT c.patient FROM Condition c WHERE c.diagnosedBy.id = :id",
                                                                Patient.class)
                                                                .setParameter("id", practitionerId)
                                                                .getResultList()
                                                                .map(patientsFromConditions -> {

                                                                        List<Patient> combined = new ArrayList<>(
                                                                                        patientsFromEncounters);
                                                                        combined.addAll(patientsFromConditions);

                                                                        return combined.stream()
                                                                                        .distinct()
                                                                                        .map(this::toPatientDTO)
                                                                                        .collect(Collectors.toList());
                                                                });
                                        });
                });
        }

        @GET
        @Path("/encounters")
        public Uni<List<EncounterDTO>> searchEncounters(
                        @QueryParam("doctorId") String doctorId,
                        @QueryParam("date") String dateString) {

                LocalDate date;
                try {
                        date = (dateString != null) ? LocalDate.parse(dateString) : LocalDate.now();
                } catch (Exception e) {
                        date = LocalDate.now();
                }

                LocalDateTime startOfDay = date.atStartOfDay();
                LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

                return Encounter.find("practitioner.userId = ?1 AND encounterDate BETWEEN ?2 AND ?3",
                                doctorId, startOfDay, endOfDay)
                                .list()
                                .map(list -> list.stream()
                                                .map(e -> (Encounter) e)
                                                .map(this::toEncounterDTO)
                                                .collect(Collectors.toList()));
        }

        // --- Helpers ---
        private PatientDTO toPatientDTO(Patient p) {
                return new PatientDTO(
                                p.id,
                                p.firstName,
                                p.lastName,
                                p.socialSecurityNumber,
                                p.dateOfBirth);
        }

        private EncounterDTO toEncounterDTO(Encounter e) {
                String patientName = (e.patient != null) ? e.patient.firstName + " " + e.patient.lastName : "Unknown";
                return new EncounterDTO(
                                e.id,
                                e.encounterDate,
                                e.notes,
                                patientName,
                                null);
        }

        private PractitionerDTO toPractitionerDTO(Practitioner p) {
                String orgName = (p.organization != null) ? p.organization.name : "N/A";
                String type = (p.type != null) ? p.type.toString() : "UNKNOWN";
                return new PractitionerDTO(
                                p.id,
                                p.firstName,
                                p.lastName,
                                type,
                                p.licenseNumber,
                                orgName);
        }
}
