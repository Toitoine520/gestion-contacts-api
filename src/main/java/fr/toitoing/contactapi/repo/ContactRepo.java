package fr.toitoing.contactapi.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.toitoing.contactapi.domain.Contact;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String>{
    Optional<Contact> findById(String id);
    Optional<Contact> findByEmail(String email);
}
