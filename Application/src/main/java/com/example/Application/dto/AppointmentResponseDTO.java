package com.example.Application.dto;

import com.example.Application.model.Appointment;

import java.time.LocalDateTime;

public class AppointmentResponseDTO {
    private Long id;
    private String doctorName;
    private LocalDateTime appointmentDate;
    private String description;
    private Long patientId;
    private PatientDTO patient;

    //Constructor
    public AppointmentResponseDTO(Appointment appointment) {
        this.id = appointment.getId();
        this.doctorName = appointment.getDoctorName();
        this.appointmentDate = appointment.getAppointmentDate();
        this.description = appointment.getDescription();
        this.patientId = appointment.getPatient().getId();
        this.patient = new PatientDTO(appointment.getPatient());
    }

    //Getters and setters


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

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }
}
