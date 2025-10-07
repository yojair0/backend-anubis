package com.anubis.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metadata")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MetadataController {

    @GetMapping("/species")
    public ResponseEntity<Map<String, Object>> getSpecies() {
        List<String> species = Arrays.asList(
            "Perro", 
            "Gato", 
            "Conejo", 
            "Ave", 
            "Reptil", 
            "Otro"
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("species", species);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/breeds")
    public ResponseEntity<Map<String, Object>> getBreeds() {
        Map<String, List<String>> breedsBySpecies = new HashMap<>();
        
        breedsBySpecies.put("Perro", Arrays.asList(
            "Labrador", "Pastor Alemán", "Golden Retriever", "Bulldog", 
            "Beagle", "Poodle", "Chihuahua", "Mestizo", "Otro"
        ));
        
        breedsBySpecies.put("Gato", Arrays.asList(
            "Siamés", "Persa", "Maine Coon", "British Shorthair",
            "Ragdoll", "Bengalí", "Mestizo", "Otro"
        ));
        
        breedsBySpecies.put("Conejo", Arrays.asList(
            "Holland Lop", "Mini Rex", "Lionhead", "Dutch",
            "Flemish Giant", "Mestizo", "Otro"
        ));
        
        breedsBySpecies.put("Ave", Arrays.asList(
            "Canario", "Periquito", "Cockatiel", "Loro",
            "Pinzón", "Cacatúa", "Otro"
        ));
        
        breedsBySpecies.put("Reptil", Arrays.asList(
            "Iguana", "Gecko", "Tortuga", "Serpiente",
            "Lagarto", "Camaleón", "Otro"
        ));
        
        Map<String, Object> response = new HashMap<>();
        response.put("breeds", breedsBySpecies);
        return ResponseEntity.ok(response);
    }
}
