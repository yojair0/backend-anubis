package com.anubis.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.anubis.model.Application;
import com.anubis.model.ApplicationStatus;

@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {
    
    List<Application> findByUserId(String userId);
    
    List<Application> findByPetId(String petId);
    
    List<Application> findByStatus(ApplicationStatus status);
    
    List<Application> findByUserIdAndStatus(String userId, ApplicationStatus status);
    
    List<Application> findByPetIdAndStatus(String petId, ApplicationStatus status);
    
    boolean existsByUserIdAndPetId(String userId, String petId);
    
    void deleteByUserId(String userId);
}
