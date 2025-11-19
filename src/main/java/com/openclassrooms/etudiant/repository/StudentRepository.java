package com.openclassrooms.etudiant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.etudiant.entities.Student;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long>{
}
