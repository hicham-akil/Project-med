package org.example.backend_med.Controller;

import org.example.backend_med.Models.Patient;
import org.example.backend_med.Services.IPatient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private IPatient iPatient;

    // Create
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient createdPatient = iPatient.createPatient(patient);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    // Read - Single by ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return iPatient.getPatientById(id)
                .map(patient -> new ResponseEntity<>(patient, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Read - All
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = iPatient.getAllPatients();
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    // Read - By Email
    @GetMapping("/email/{email}")
    public ResponseEntity<Patient> getPatientByEmail(@PathVariable String email) {
        return iPatient.getPatientByEmail(email)
                .map(patient -> new ResponseEntity<>(patient, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Read - By Telephone
    @GetMapping("/telephone/{telephone}")
    public ResponseEntity<Patient> getPatientByTelephone(@PathVariable String telephone) {
        return iPatient.getPatientByTelephone(telephone)
                .map(patient -> new ResponseEntity<>(patient, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Read - Search by Name
    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatientsByName(@RequestParam String name) {
        List<Patient> patients = iPatient.searchPatientsByName(name);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }


    // Update
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        try {
            Patient updatedPatient = iPatient.updatePatient(id, patient);
            return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        try {
            iPatient.deletePatient(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Check existence by ID
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Long id) {
        boolean exists = iPatient.existsById(id);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    // Check existence by Email
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = iPatient.existsByEmail(email);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    // Count
    @GetMapping("/count")
    public ResponseEntity<Long> countPatients() {
        long count = iPatient.countPatients();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}