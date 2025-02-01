package com.example.Application.controller;

import com.example.Application.dto.BasicAppointmentResponseDTO;
import com.example.Application.dto.PatientDTO;
import com.example.Application.model.Patient;
import com.example.Application.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.*;

@RestController
@RequestMapping("/patients")
public class PatientController {
    @Autowired
    private PatientRepository patientRepository;


    //Getting all patients
    @GetMapping
    public ResponseEntity<?> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        //Validation for pagination parameters
        if(page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body("Page number must be >= 0 and size must be > 0");
        }

        //Fetch patients with metadata
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Patient> patients = patientRepository.findAll(pageRequest);


        //Create response with metadata
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("patients", patients.stream().map(PatientDTO::new).toList());
        response.put("currentPage", patients.getNumber());
        response.put("totalItems", patients.getTotalElements());
        response.put("totalPages", patients.getTotalPages());

        return ResponseEntity.ok(response);
    }

    //Getting patient by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientByID(@PathVariable Long id) {
        PatientDTO patient = new PatientDTO(patientRepository.findById(id).orElseThrow());

        return ResponseEntity.ok(patient);
    }

    //Getting a patient's appointments
    @GetMapping("/{id}/appointments")
    public ResponseEntity<?> getPatientAppointmentsById(@PathVariable Long id) {
        if(!patientRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
        }

        //Fetch the patient
        Patient patient = patientRepository.findById(id).orElseThrow();

        //Convert appointments to DTOs
        List<BasicAppointmentResponseDTO> appointmentResponseDTOs = patient.getAppointments()
                .stream()
                .map(BasicAppointmentResponseDTO::new)
                .toList();

        //Build response
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("patientId", patient.getId());
        response.put("name", patient.getName() + " " + patient.getSurname());
        response.put("appointments", appointmentResponseDTOs);

        return ResponseEntity.ok(response);

    }

    //Patient creating
    @PostMapping
    public ResponseEntity<?> createPatient(@RequestBody @Valid Patient patient, BindingResult result) {
        if(result.hasErrors()) {
            List<String> errorMessages = new ArrayList<>();

            for (ObjectError error : result.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(errorMessages);
        }

        Patient savedPatient = patientRepository.save(patient);
        return ResponseEntity.ok(savedPatient);
    }


    //Delete a patient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (!patientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        patientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //Patient update
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient updatedPatient) {
        if(!patientRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); //Patient not found
        }

        //Find the existing patient
        Patient existingPatient = patientRepository.findById(id).orElseThrow();

        //Update fields
        existingPatient.setName(updatedPatient.getName());
        existingPatient.setAge(updatedPatient.getAge());
        existingPatient.setEmail(updatedPatient.getEmail());

        //Saved updated patient
        Patient savedPatient = patientRepository.save(existingPatient);

        return ResponseEntity.ok(savedPatient);
    }

    //Patient's certain field(s) update
    @PatchMapping("/{id}")
    public ResponseEntity<Patient> updatePatientsCertainField(@PathVariable Long id, @RequestBody Patient updatedPatient) {
        if(!patientRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); //Patient not found
        }

        //Find the existing patient
        Patient existingPatient = patientRepository.findById(id).orElseThrow();

        // Update only non-null fields using Reflection
        for (Field field : Patient.class.getDeclaredFields()) {
            field.setAccessible(true); // Make private fields accessible
            try {
                Object value = field.get(updatedPatient); // Get the value from the incoming object
                if (value != null) {
                    field.set(existingPatient, value); // Set the value in the existing object
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // Handle reflection errors
            }
        }

        if(!updatedPatient.getName().isEmpty()) {
            existingPatient.setName(updatedPatient.getName());
        } else if (!updatedPatient.getEmail().isEmpty()) {
            existingPatient.setEmail(updatedPatient.getEmail());
        } else if(!updatedPatient.getAge().toString().isEmpty()) {
            existingPatient.setAge(updatedPatient.getAge());
        }

        Patient savedPatient = patientRepository.save(existingPatient);

        return ResponseEntity.ok(savedPatient);
    }


    //Searching patient(s)
    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam(required = false) String name,
                                                        @RequestParam (required = false) String surname,
                                                        @RequestParam(required = false) Integer age) {
        List<Patient> patients;

        if(name != null && age != null) {
            patients = patientRepository.findByNameAndAge(name, age); //Custom query for both name and age
        } else if(name != null) {
            patients = patientRepository.findByNameContainingIgnoreCase(name); //Custom query for name
        } else if(age != null) {
            patients = patientRepository.findByAge(age); //Custom query for age
        } else if(surname != null) {
            patients = patientRepository.findBySurnameContainingIgnoreCase(surname); //Custom query for surname
        } else {
            patients = patientRepository.findAll(); //Return all patients if no parameters provide
        }

        if(patients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(patients);
    }

}
