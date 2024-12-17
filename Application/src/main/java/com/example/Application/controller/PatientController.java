package com.example.Application.controller;

import com.example.Application.model.Patient;
import com.example.Application.repository.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/patients")
public class PatientController {
    @Autowired
    private PatientRepository patientRepository;

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientByID(@PathVariable Long id) {
        Optional<Patient> patientOptional = patientRepository.findById(id);

        if (patientOptional.isPresent()) {
            return ResponseEntity.ok(patientOptional.get());
        } else {
            return ResponseEntity.status(404).body("Patient with ID " + id + " not found");
        }
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (!patientRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        patientRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

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

    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam(required = false) String name, @RequestParam(required = false) Integer age) {
        List<Patient> patients;

        if(name != null && age != null) {
            patients = patientRepository.findByNameAndAge(name, age); //Custom query for both name and age
        } else if(name != null) {
            patients = patientRepository.findByName(name); //Custom query for name
        } else if(age != null) {
            patients = patientRepository.findByAge(age); //Custom query for age
        } else {
            patients = patientRepository.findAll(); //Return all patients if no parameters provide
        }

        if(patients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(patients);
    }

}
