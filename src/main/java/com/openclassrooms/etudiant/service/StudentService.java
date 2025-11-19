package com.openclassrooms.etudiant.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.openclassrooms.etudiant.entities.Student;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StudentService {
    // Save operation
    public Student saveStudent(Student student) {
        return null;
    }

    // Read operation
    public List<Student> fetchStudentList() {
        return null;
    }

    // Update operation
    public Student updateStudent(Student student, Long studentId) {
        return null;
    }

    // Delete operation
    public void deleteStudentById(Long studentId) {
    }
}
