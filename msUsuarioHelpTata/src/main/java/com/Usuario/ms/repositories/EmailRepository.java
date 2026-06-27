package com.Usuario.ms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Usuario.ms.models.entities.Email;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<Email, Integer> {

    @Query("SELECT e FROM Email e WHERE e.email = :email")
    Optional<Email> findByEmailAddress(@Param("email") String email);
}
