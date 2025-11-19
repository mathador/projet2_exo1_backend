package com.openclassrooms.etudiant.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    // Save operation
    public Student saveStudent(Student student) {
        Assert.notNull(student, "Student must not be null");
        log.info("Registering new student");
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        return studentRepository.save(student);
    }

    // Read operation
    public List<Student> fetchStudentList() {
        List<Student> list = new ArrayList<>();
        studentRepository.findAll().forEach(list::add);
        return list;
    }

    // Update operation
    public Student updateStudent(Student student, Long studentId) {
        Optional<Student> existing = studentRepository.findById(studentId);
        if (existing.isPresent()) {
            Student s = existing.get();
            s.setUpdatedAt(LocalDateTime.now());
            s.setLevel(student.getLevel());
            s.setMatter(student.getMatter());
            return studentRepository.save(s);
        }
        throw new IllegalArgumentException("Student with id " + studentId + " not found");
    }

    // Delete operation
    public void deleteStudentById(Long studentId) {
        studentRepository.deleteById(studentId);
    }
}
