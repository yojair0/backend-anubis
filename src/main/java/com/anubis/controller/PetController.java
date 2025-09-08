package com.anubis.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anubis.dto.PetRequest;
import com.anubis.model.Pet;
import com.anubis.model.PetStatus;
import com.anubis.security.UserPrincipal;
import com.anubis.service.PetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PetController {

    @Autowired
    private PetService petService;

    @GetMapping
    public ResponseEntity<?> getAllPets() {
        try {
            List<Pet> pets = petService.getAvailablePets();
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPetById(@PathVariable String id) {
        try {
            Optional<Pet> pet = petService.getPetById(id);
            if (pet.isPresent()) {
                return ResponseEntity.ok(pet.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/species/{species}")
    public ResponseEntity<?> getPetsBySpecies(@PathVariable String species) {
        try {
            List<Pet> pets = petService.getPetsBySpecies(species);
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('FOUNDATION') or hasRole('ADMIN')")
    public ResponseEntity<?> createPet(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PetRequest petRequest) {
        try {
            Pet pet = new Pet();
            pet.setName(petRequest.getName());
            pet.setSpecies(petRequest.getSpecies());
            pet.setBreed(petRequest.getBreed());
            pet.setAge(petRequest.getAge());
            pet.setGender(petRequest.getGender());
            pet.setSize(petRequest.getSize());
            pet.setDescription(petRequest.getDescription());
            pet.setImageUrls(petRequest.getImageUrls());
            pet.setFoundationId(userPrincipal.getId());

            Pet savedPet = petService.createPet(pet);
            return ResponseEntity.ok(savedPet);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FOUNDATION') or hasRole('ADMIN')")
    public ResponseEntity<?> updatePet(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PetRequest petRequest) {
        try {
            // Verificar que la mascota pertenece a la fundación
            Optional<Pet> existingPet = petService.getPetById(id);
            if (existingPet.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (!existingPet.get().getFoundationId().equals(userPrincipal.getId()) && 
                !userPrincipal.getAuthorities().toString().contains("ADMIN")) {
                return ResponseEntity.status(403)
                    .body(new MessageResponse("No tienes permisos para editar esta mascota"));
            }

            Pet pet = new Pet();
            pet.setName(petRequest.getName());
            pet.setSpecies(petRequest.getSpecies());
            pet.setBreed(petRequest.getBreed());
            pet.setAge(petRequest.getAge());
            pet.setGender(petRequest.getGender());
            pet.setSize(petRequest.getSize());
            pet.setDescription(petRequest.getDescription());
            pet.setImageUrls(petRequest.getImageUrls());

            Pet updatedPet = petService.updatePet(id, pet);
            return ResponseEntity.ok(updatedPet);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('FOUNDATION') or hasRole('ADMIN')")
    public ResponseEntity<?> updatePetStatus(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam PetStatus status) {
        try {
            Optional<Pet> existingPet = petService.getPetById(id);
            if (existingPet.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (!existingPet.get().getFoundationId().equals(userPrincipal.getId()) && 
                !userPrincipal.getAuthorities().toString().contains("ADMIN")) {
                return ResponseEntity.status(403)
                    .body(new MessageResponse("No tienes permisos para cambiar el estado de esta mascota"));
            }

            Pet updatedPet = petService.updatePetStatus(id, status);
            return ResponseEntity.ok(updatedPet);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FOUNDATION') or hasRole('ADMIN')")
    public ResponseEntity<?> deletePet(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Optional<Pet> existingPet = petService.getPetById(id);
            if (existingPet.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (!existingPet.get().getFoundationId().equals(userPrincipal.getId()) && 
                !userPrincipal.getAuthorities().toString().contains("ADMIN")) {
                return ResponseEntity.status(403)
                    .body(new MessageResponse("No tienes permisos para eliminar esta mascota"));
            }

            petService.deletePet(id);
            return ResponseEntity.ok(new MessageResponse("Mascota eliminada exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/foundation/my-pets")
    @PreAuthorize("hasRole('FOUNDATION')")
    public ResponseEntity<?> getMyPets(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            List<Pet> pets = petService.getPetsByFoundation(userPrincipal.getId());
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Clase interna para respuestas de mensaje
    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
