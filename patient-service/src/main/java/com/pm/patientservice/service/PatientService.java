package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {

        this.patientRepository = patientRepository;

    }


    public List<PatientResponseDTO> getPatients(){

        List<Patient> patients = patientRepository.findAll();

        return patients.stream().map(patient -> PatientMapper.toDTO(patient)).toList();
    }



    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){

        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())){

            throw new EmailAlreadyExistsException("A patient of this email already exists");
        }

        Patient newPatient = patientRepository.save(PatientMapper.toEntity(patientRequestDTO));

        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){

        Patient patient = patientRepository.findById(id).orElseThrow(
                ()-> new PatientNotFoundException("Patient not found with ID: " + id));

        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())){

            throw new EmailAlreadyExistsException("A patient of this email already exists");
        }

        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setAddress(patientRequestDTO.getAddress());

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id){

        patientRepository.deleteById(id);
    }



}
