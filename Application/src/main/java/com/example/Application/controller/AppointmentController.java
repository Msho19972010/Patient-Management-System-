package com.example.Application.controller;

import com.example.Application.dto.BasicAppointmentResponseDTO;
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
    public ResponseEntity<List<BasicAppointmentResponseDTO>> getAllAppointments() {
        List<BasicAppointmentResponseDTO> appointments = appointmentRepository.findAll().stream().map(BasicAppointmentResponseDTO::new).toList();
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
        BasicAppointmentResponseDTO response = new BasicAppointmentResponseDTO(savedAppointment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Get appointments by specific patient
    @GetMapping("/{id}")
    public ResponseEntity<List<BasicAppointmentResponseDTO>> getAppointmentsByPatient(@PathVariable Long id) {
        if(!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient with ID: " + id + " not found");
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);
        List<BasicAppointmentResponseDTO> response = appointments.stream()
                .map(BasicAppointmentResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    //Update an appointment
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @Valid @RequestBody Appointment updatedAppointment) {
        if(!appointmentRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("The appointment's ID: " + id + " doesn't exist");
        }

        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment with ID: " + id + " not found")
        );

        //Update fields conditionally
        if(updatedAppointment.getDoctorName() != null) {
            existingAppointment.setDoctorName(updatedAppointment.getDoctorName());
        }
        //Handle patient update with validation
        if(updatedAppointment.getPatient() != null && updatedAppointment.getPatient().getId() != null) {
            Long patientId = updatedAppointment.getPatient().getId();
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient with ID: " + patientId + " not found"));
            existingAppointment.setPatient(patient);
        }
        if(updatedAppointment.getDescription() != null) {
            existingAppointment.setDescription(updatedAppointment.getDescription());
        }
        if(updatedAppointment.getAppointmentDate() != null) {
            existingAppointment.setAppointmentDate(updatedAppointment.getAppointmentDate());
        }

        Appointment savedAppointment = appointmentRepository.save(existingAppointment);
        BasicAppointmentResponseDTO response = new BasicAppointmentResponseDTO(savedAppointment);
        return ResponseEntity.ok(response);
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
