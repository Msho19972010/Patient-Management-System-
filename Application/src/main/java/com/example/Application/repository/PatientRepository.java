package com.example.Application.repository;

import com.example.Application.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByNameAndAge(String name, Integer age); //Fined patients by name and age
    List<Patient> findByName(String name); //Find patients by name
    List<Patient> findByAge(Integer age); //Find patients by age
}