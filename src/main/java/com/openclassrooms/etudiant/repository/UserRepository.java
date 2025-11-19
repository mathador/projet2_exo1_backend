package com.openclassrooms.etudiant.repository;

import com.openclassrooms.etudiant.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by their login.
     * 
     * @param login
     * @return
     */
    Optional<User> findByLogin(String login);
}
