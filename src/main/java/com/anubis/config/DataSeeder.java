package com.anubis.config;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.anubis.model.Pet;
import com.anubis.model.PetStatus;
import com.anubis.repository.PetRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private PetRepository petRepository;

    @Override
    public void run(String... args) throws Exception {
        if (petRepository.count() == 0) {
            createSamplePets();
        }
    }

    private void createSamplePets() {
        List<Pet> samplePets = Arrays.asList(
            createPet("Max", "Perro", "Labrador", 3, "Macho", "Grande", 
                "Max es un labrador muy cariñoso y juguetón. Le encanta correr y jugar con la pelota. Es perfecto para familias con niños.",
                Arrays.asList("https://example.com/max1.jpg", "https://example.com/max2.jpg")),
                
            createPet("Luna", "Gato", "Siamés", 2, "Hembra", "Mediano",
                "Luna es una gata siamesa muy elegante y tranquila. Le gusta dormir al sol y es muy independiente.",
                Arrays.asList("https://example.com/luna1.jpg")),
                
            createPet("Rocky", "Perro", "Pastor Alemán", 5, "Macho", "Grande",
                "Rocky es un pastor alemán muy leal y protector. Está bien entrenado y es excelente como guardián.",
                Arrays.asList("https://example.com/rocky1.jpg", "https://example.com/rocky2.jpg")),
                
            createPet("Mia", "Gato", "Persa", 1, "Hembra", "Pequeño",
                "Mia es una gatita persa muy tierna y cariñosa. Le encanta que la mimen y es perfecta para apartamentos.",
                Arrays.asList("https://example.com/mia1.jpg")),
                
            createPet("Bruno", "Perro", "Bulldog Francés", 4, "Macho", "Mediano",
                "Bruno es un bulldog francés muy divertido y sociable. Se lleva bien con otros perros y niños.",
                Arrays.asList("https://example.com/bruno1.jpg", "https://example.com/bruno2.jpg")),
                
            createPet("Cleo", "Gato", "Mestizo", 3, "Hembra", "Mediano",
                "Cleo es una gata mestiza muy inteligente y curiosa. Le gusta explorar y es muy activa.",
                Arrays.asList("https://example.com/cleo1.jpg"))
        );

        petRepository.saveAll(samplePets);
        System.out.println("Datos de prueba creados: " + samplePets.size() + " mascotas");
    }

    private Pet createPet(String name, String species, String breed, Integer age, 
                         String gender, String size, String description, List<String> imageUrls) {
        Pet pet = new Pet();
        pet.setName(name);
        pet.setSpecies(species);
        pet.setBreed(breed);
        pet.setAge(age);
        pet.setGender(gender);
        pet.setSize(size);
        pet.setDescription(description);
        pet.setImageUrls(imageUrls);
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setFoundationId("sample-foundation-id");
        pet.setCreatedAt(LocalDateTime.now());
        pet.setUpdatedAt(LocalDateTime.now());
        pet.setActive(true);
        return pet;
    }
}
