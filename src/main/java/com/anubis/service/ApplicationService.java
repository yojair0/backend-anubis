package com.anubis.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anubis.dto.ApplicationDetailResponse;
import com.anubis.dto.ApplicationRequest;
import com.anubis.dto.ApplicationStatusRequest;
import com.anubis.model.Application;
import com.anubis.model.ApplicationStatus;
import com.anubis.model.Pet;
import com.anubis.model.PetStatus;
import com.anubis.model.User;
import com.anubis.repository.ApplicationRepository;
import com.anubis.repository.PetRepository;
import com.anubis.repository.UserRepository;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public Application createApplication(String userId, ApplicationRequest request) {
        Pet pet = petRepository.findById(request.getPetId())
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        if (pet.getStatus() != PetStatus.AVAILABLE) {
            throw new RuntimeException("La mascota no está disponible para adopción");
        }

        if (applicationRepository.existsByUserIdAndPetId(userId, request.getPetId())) {
            throw new RuntimeException("Ya has postulado para esta mascota");
        }

        Application application = new Application(userId, request.getPetId(), request.getReason(), 
                                                 request.getExperience(), request.getLivingSpace(), 
                                                 request.getHasOtherPets(), request.getWorkSchedule());
        
        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsByUser(String userId) {
        return applicationRepository.findByUserId(userId);
    }

    public List<Application> getApplicationsByPet(String petId) {
        return applicationRepository.findByPetId(petId);
    }

    public List<Application> getApplicationsByFoundation(String foundationId) {
        List<Pet> foundationPets = petRepository.findByFoundationId(foundationId);
        
        return foundationPets.stream()
            .flatMap(pet -> applicationRepository.findByPetId(pet.getId()).stream())
            .toList();
    }

    public Application updateApplicationStatus(String applicationId, String foundationId, ApplicationStatusRequest request) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));

        Pet pet = petRepository.findById(application.getPetId())
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        if (!pet.getFoundationId().equals(foundationId)) {
            throw new RuntimeException("No tienes permisos para modificar esta postulación");
        }

        application.setStatus(request.getStatus());
        application.setFoundationResponse(request.getFoundationResponse());
        application.setUpdatedAt(LocalDateTime.now());

        // Si se acepta la postulación, cambiar el estado de la mascota
        if (request.getStatus() == ApplicationStatus.ACCEPTED) {
            pet.setStatus(PetStatus.IN_PROCESS);
            pet.setUpdatedAt(LocalDateTime.now());
            petRepository.save(pet);

            // Rechazar automáticamente otras postulaciones pendientes para esta mascota
            List<Application> otherApplications = applicationRepository.findByPetIdAndStatus(
                application.getPetId(), ApplicationStatus.PENDING);
            
            for (Application otherApp : otherApplications) {
                if (!otherApp.getId().equals(applicationId)) {
                    otherApp.setStatus(ApplicationStatus.REJECTED);
                    otherApp.setFoundationResponse("La mascota ya fue asignada a otro adoptante");
                    otherApp.setUpdatedAt(LocalDateTime.now());
                    applicationRepository.save(otherApp);
                    
                    // Enviar notificación de rechazo a otros aplicantes
                    User otherUser = userRepository.findById(otherApp.getUserId()).orElse(null);
                    if (otherUser != null) {
                        emailService.sendApplicationStatusEmail(
                            otherUser.getEmail(),
                            otherUser.getFullName(),
                            pet.getName(),
                            "REJECTED",
                            "La mascota ya fue asignada a otro adoptante"
                        );
                    }
                }
            }
        }

        // Enviar notificación al aplicante principal
        User user = userRepository.findById(application.getUserId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        emailService.sendApplicationStatusEmail(
            user.getEmail(),
            user.getFullName(),
            pet.getName(),
            request.getStatus().toString(),
            request.getFoundationResponse()
        );

        return applicationRepository.save(application);
    }

    public Optional<Application> getApplicationById(String applicationId) {
        return applicationRepository.findById(applicationId);
    }

    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status);
    }

    public long countApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status).size();
    }

    public List<ApplicationDetailResponse> getDetailedApplicationsByUser(String userId) {
        List<Application> applications = applicationRepository.findByUserId(userId);
        return applications.stream()
            .map(app -> {
                Pet pet = petRepository.findById(app.getPetId()).orElse(null);
                User user = userRepository.findById(app.getUserId()).orElse(null);
                return new ApplicationDetailResponse(app, pet, user);
            })
            .collect(Collectors.toList());
    }

    public List<ApplicationDetailResponse> getDetailedApplicationsByFoundation(String foundationId) {
        List<Pet> foundationPets = petRepository.findByFoundationId(foundationId);
        return foundationPets.stream()
            .flatMap(pet -> applicationRepository.findByPetId(pet.getId()).stream()
                .map(app -> {
                    User user = userRepository.findById(app.getUserId()).orElse(null);
                    return new ApplicationDetailResponse(app, pet, user);
                }))
            .collect(Collectors.toList());
    }

    public Optional<ApplicationDetailResponse> getDetailedApplicationById(String applicationId) {
        Optional<Application> applicationOpt = applicationRepository.findById(applicationId);
        if (applicationOpt.isPresent()) {
            Application application = applicationOpt.get();
            Pet pet = petRepository.findById(application.getPetId()).orElse(null);
            User user = userRepository.findById(application.getUserId()).orElse(null);
            return Optional.of(new ApplicationDetailResponse(application, pet, user));
        }
        return Optional.empty();
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public List<ApplicationDetailResponse> getAllDetailedApplications() {
        return applicationRepository.findAll().stream()
            .map(app -> {
                Pet pet = petRepository.findById(app.getPetId()).orElse(null);
                User user = userRepository.findById(app.getUserId()).orElse(null);
                return new ApplicationDetailResponse(app, pet, user);
            })
            .collect(Collectors.toList());
    }
}
