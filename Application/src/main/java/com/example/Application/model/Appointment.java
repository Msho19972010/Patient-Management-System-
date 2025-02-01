package com.example.Application.model;

import com.example.Application.dto.PatientDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String doctorName;
    @NotNull(message = "This row can't be null")
    @Future(message = "Appointed date must be in the future")
    private LocalDateTime appointmentDate;
    @NotEmpty(message = "Description is required")
    private String description;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    //Constructor
    public Appointment(){};
    public Appointment(Long id, String doctorName, LocalDateTime appointmentDate, String description, Patient patient) {
        this.id = id;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.description = description;
        this.patient = patient;
    }

    //Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
