package com.example.Application.controller;

import com.example.Application.dto.AppointmentResponseDTO;
import com.example.Application.exceptions.ResourceNotFoundException;
import com.example.Application.model.Appointment;
import com.example.Application.model.Patient;
import com.example.Application.repository.AppointmentRepository;
import com.example.Application.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    //Get all appointments
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return ResponseEntity.ok(appointments);
    }

    //Create a new appointment
    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody Appointment appointment) {
        //Fetch the patient and validate existence
        Patient patient = patientRepository.findById(appointment.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient with ID: " + appointment.getPatient().getId() + " not found."));

        //Assign the patient to the appointment
        appointment.setPatient(patient);

        //Save the appointment
        Appointment savedAppointment = appointmentRepository.save(appointment);

        //Map to ResponseDTO
        AppointmentResponseDTO response = new AppointmentResponseDTO(savedAppointment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Get appointments by specific patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getAppointmentsByPatient(@PathVariable Long patientId) {
        if(!patientRepository.existsById(patientId)) {
            return ResponseEntity.badRequest().body("Invalid ID");
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return ResponseEntity.ok(appointments);
    }

    //Update an appointment
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @Valid @RequestBody Appointment updatedAppointment) {
        if(!appointmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Appointment existingAppointment = appointmentRepository.findById(id).orElseThrow();

        //Update fields
        existingAppointment.setDoctorName(updatedAppointment.getDoctorName());
        existingAppointment.setPatient(updatedAppointment.getPatient());
        existingAppointment.setDescription(updatedAppointment.getDescription());
        existingAppointment.setAppointmentDate(updatedAppointment.getAppointmentDate());

        Appointment savedAppointment = appointmentRepository.save(existingAppointment);
        return ResponseEntity.ok(savedAppointment);
    }

    //Delete an appointment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        if(!appointmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }


        appointmentRepository.deleteById(id);
        return ResponseEntity.ok().body("Appointment deleted successfully");
    }
}
