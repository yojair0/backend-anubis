package com.anubis.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.anubis.model.PendingRegistration;

@Repository
public interface PendingRegistrationRepository extends MongoRepository<PendingRegistration, String> {
    
    Optional<PendingRegistration> findByEmail(String email);
    
    Optional<PendingRegistration> findByEmailAndVerificationCode(String email, String verificationCode);
    
    List<PendingRegistration> findByExpiryDateBefore(LocalDateTime dateTime);
    
    List<PendingRegistration> findByVerified(boolean verified);
    
    void deleteByEmail(String email);
    
    void deleteByExpiryDateBefore(LocalDateTime dateTime);
    
    boolean existsByEmail(String email);
}