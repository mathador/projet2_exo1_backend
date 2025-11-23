package com.openclassrooms.etudiant.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.service.StudentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    // Save operation
    @PostMapping("/api/students")
    public Student saveStudent(@Valid @RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    // Read operation
    @GetMapping("/api/students")

    public List<Student> fetchStudentList() {
        return studentService.fetchStudentList();
    }

    // Read operation
    @GetMapping("/api/students/{id}")
    public Optional<Student> getStudentById(@PathVariable("id") Long studentId) {
        return studentService.getStudentById(studentId);
    }

    // Update operation
    @PutMapping("/api/students/{id}")
    public Student updateStudent(@RequestBody Student student,
            @PathVariable("id") Long studentId) {
        return studentService.updateStudent(student, studentId);
    }

    // Delete operation
    @DeleteMapping("/api/students/{id}")
    public String deleteStudentById(@PathVariable("id") Long studentId) {
        studentService.deleteStudentById(studentId);
        return "Deleted Successfully";
    }
}
