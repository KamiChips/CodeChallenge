package mx.edu.cetys.software_quality_lab.petstore;

import mx.edu.cetys.software_quality_lab.commons.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/petstore")
public class AdoptionController {

    // DTOs — definen la forma del request y del response para el módulo de adopciones
    record AdoptionRequest(Long userId, Long petId) {}
    record AdoptionResponse(Long id, Long userId, Long petId, String status, String adoptionDate) {}
    record AdoptionWrapper(AdoptionResponse adoption) {}

    // DTO para listar pets disponibles — vista simplificada del Pet para el petstore
    record AvailablePetResponse(Long id, String name, String race, String color, Integer age) {}

    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    // GET /petstore/pets — listar todos los pets disponibles para adopción
    @GetMapping("/pets")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<List<AvailablePetResponse>> listAvailablePets() {
        var pets = adoptionService.listAvailablePets();
        return new ApiResponse<>("Pets disponibles: " + pets.size(), pets, null);
    }

    // POST /petstore/adoptions — crear una nueva adopción
    @PostMapping("/adoptions")
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<AdoptionWrapper> createAdoption(@RequestBody AdoptionRequest request) {
        return new ApiResponse<>("Adopcion creada exitosamente",
                new AdoptionWrapper(adoptionService.createAdoption(request)), null);
    }

    // PATCH /petstore/adoptions/{id}/cancel — cancelar una adopción activa
    @PatchMapping("/adoptions/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<AdoptionWrapper> cancelAdoption(@PathVariable Long id) {
        return new ApiResponse<>("Adopción cancelada exitosamente",
                new AdoptionWrapper(adoptionService.cancelAdoption(id)), null);
    }
}
