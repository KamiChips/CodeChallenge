package mx.edu.cetys.software_quality_lab.petstore;

import mx.edu.cetys.software_quality_lab.pets.PetRepository;
import mx.edu.cetys.software_quality_lab.pets.exceptions.PetNotFoundException;
import mx.edu.cetys.software_quality_lab.petstore.exceptions.AdoptionNotFoundException;
import mx.edu.cetys.software_quality_lab.petstore.exceptions.MaxAdoptionsReachedException;
import mx.edu.cetys.software_quality_lab.petstore.exceptions.PetAlreadyAdoptedException;
import mx.edu.cetys.software_quality_lab.petstore.exceptions.UserNotEligibleException;
import mx.edu.cetys.software_quality_lab.users.UserRepository;
import mx.edu.cetys.software_quality_lab.users.UserStatus;
import mx.edu.cetys.software_quality_lab.users.exceptions.InvalidUserDataException;
import mx.edu.cetys.software_quality_lab.users.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdoptionService {

    private final Logger log = LoggerFactory.getLogger(AdoptionService.class);

    private final AdoptionRepository adoptionRepository;
    private final UserRepository userRepository;
    // petstore reutiliza el PetRepository del módulo pets/ — no duplica entidades
    private final PetRepository petRepository;

    public AdoptionService(AdoptionRepository adoptionRepository,
                           UserRepository userRepository,
                           PetRepository petRepository) {
        this.adoptionRepository = adoptionRepository;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
    }

    /**
     * Listar todos los pets disponibles para adopción (available = true).
     */
    List<AdoptionController.AvailablePetResponse> listAvailablePets() {
        log.info("Obteniendo pets disponibles para adopción");
        return petRepository.findAllByAvailableTrue()
                .stream()
                .map(pet -> new AdoptionController.AvailablePetResponse(
                        pet.getId(), pet.getName(), pet.getRace(), pet.getColor(), pet.getAge()))
                .toList();
    }

    /**
     * Crear una adopción vinculando un usuario con un pet.
     *
     * Reglas a implementar (usar la excepción indicada en cada caso):
     *  1. El usuario debe existir                              → UserNotFoundException
     *  2. El status del usuario debe ser ACTIVE                → UserNotEligibleException
     *  3. La edad del usuario debe ser >= 18                   → UserNotEligibleException
     *  4. El pet debe existir                                  → PetNotFoundException
     *  5. El pet no debe tener una adopción ACTIVE             → PetAlreadyAdoptedException
     *  6. El usuario debe tener menos de 3 adopciones ACTIVE   → MaxAdoptionsReachedException
     *
     * En caso de éxito: marcar pet.available = false, guardar el pet, guardar la adopción y regresar respuesta.
     */
    AdoptionController.AdoptionResponse createAdoption(AdoptionController.AdoptionRequest request) {
        log.info("Creando adopción, userId={}, petId={}", request.userId(), request.petId());

        //Regla 1
        var formatUser = userRepository.findById(request.userId());
        if (formatUser.isEmpty()) {
            //throw new UserNotFoundException("Usuario con id " + request.userId() + " no encontrado");
        }

        var user  = formatUser.get();

        // Regla 2
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserNotEligibleException("Usuario con id " + request.userId() + " no encontrado");
        }

        // Regla 3
        if (user.getAge() < 18) {
            throw new UserNotEligibleException("El usario debe de tener al menos 18 años para adoptar");
        }

        // Regla 4
        var formPet = petRepository.findById(request.petId());
        if (formPet.isEmpty()) {
            throw new PetNotFoundException("Pet " + request.petId() + " no encontrado");
        }

        var pet = formPet.get();

        // Regla 5
        if (adoptionRepository.existsByPetIdAndStatus(pet.getId(), AdoptionStatus.ACTIVE)) {
            throw new PetAlreadyAdoptedException("El pet con id " + request.petId() + " ya se encuentra en adopcion activa");
        }

        // Regla 6
        if (adoptionRepository.countByUserIdAndStatus(user.getId(), AdoptionStatus.ACTIVE) >=3) {
            throw new MaxAdoptionsReachedException("El usuario ya tiene 3 adopciones activas");
        }

        // Marcar pet no disponible y guardar
        pet.setAvailable(false);
        petRepository.save(pet);

        var saved = adoptionRepository.save( new Adoption(user, pet));
        log.info("Adopcion creada existosamente, id{}", saved.getId());
        return mapToResponse(saved);

    }

    /**
     * Cancelar una adopción existente.
     *
     * Reglas a implementar:
     *  1. La adopción debe existir        → AdoptionNotFoundException
     *  2. La adopción debe estar ACTIVE   → InvalidUserDataException
     *
     * En caso de éxito: cambiar adoption.status = CANCELLED, marcar pet.available = true, guardar ambos.
     */
    AdoptionController.AdoptionResponse cancelAdoption(Long adoptionId) {
        log.info("Cancelando adopción, adoptionId={}", adoptionId);

        // Regla 1
        var formAdoption = adoptionRepository.findById(adoptionId);
        if (formAdoption.isEmpty()) {
            throw new AdoptionNotFoundException("Adoption " + adoptionId + " no encontrado");
        }

        var adoption = formAdoption.get();

        // Regla 2
        if (adoption.getStatus() == AdoptionStatus.CANCELLED) {
            //throw new InvalidUserDataException("La adopción ya está cancelada");
        }

        adoption.setStatus(AdoptionStatus.CANCELLED);
        adoption.getPet().setAvailable(true);
        petRepository.save(adoption.getPet());

        var saved = adoptionRepository.save(adoption);
        log.info("Adopcion cancelada existosamente, id{}", saved.getId());
        return mapToResponse(saved);
    }

    private AdoptionController.AdoptionResponse mapToResponse(Adoption adoption) {
        return  new AdoptionController.AdoptionResponse(
                adoption.getId(),
                adoption.getUser().getId(),
                adoption.getPet().getId(),
                adoption.getStatus().name(),
                adoption.getAdoptionDate().toString()
        );
    }
}
