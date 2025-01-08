package com.example.Application.repository;

import com.example.Application.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    //Custom query to find appointments by doctor name
    List<Appointment> findByDoctorName(String doctorName);

    //Custom query to find appointment by patient ID
    List<Appointment> findByPatientId(Long patientId);
}
