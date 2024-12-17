package com.example.Application.controller;

import com.example.Application.model.Appointment;
import com.example.Application.model.Patient;
import com.example.Application.repository.AppointmentRepository;
import com.example.Application.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @PostMapping
    public Appointment createAppointment(@RequestBody Appointment appointment) {
        Long patientId = appointment.getPatient().getId();
        Patient patient = patientRepository.findById(patientId).
                orElseThrow(() -> new RuntimeException("Patient not found"));
        appointment.setPatient(patient);
        return appointmentRepository.save(appointment);
    }

    @DeleteMapping
    void deleteAppointment(@PathVariable Long id) {
        appointmentRepository.deleteById(id);
    }
}
