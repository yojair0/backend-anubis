package com.anubis.repository;

import com.anubis.model.Pet;
import com.anubis.model.PetStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends MongoRepository<Pet, String> {
    
    List<Pet> findByActiveTrue();
    
    List<Pet> findByFoundationId(String foundationId);
    
    List<Pet> findByFoundationIdAndActiveTrue(String foundationId);
    
    List<Pet> findByStatus(PetStatus status);
    
    List<Pet> findByStatusAndActiveTrue(PetStatus status);
    
    List<Pet> findBySpeciesAndActiveTrue(String species);
}
