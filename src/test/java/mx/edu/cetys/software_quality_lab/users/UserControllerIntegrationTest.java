package mx.edu.cetys.software_quality_lab.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static mx.edu.cetys.software_quality_lab.users.UserStatus.SUSPENDED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    // Limpiar la BD antes de cada prueba para garantizar un estado independiente
    @BeforeEach
    public void limpiarBD() {
        userRepository.deleteAll();
    }

    private static final String valid_body = """
                {"username":"abcd4",
                "firstName":"Mayrin",
                "lastName":"Peredia",
                "phone":"6461234567",
                "email":"mayrin4#hola.com",
                "age":20 }""";

    // ─── POST /users ──────────────────────────────────────────────────────────

    @Test
    void shouldCreateUserAndReturn201() throws Exception {
        // El email sigue el formato del EmailValidatorService: usuario#proveedor.dominio
        String body = """
                {
                    "username": "juan4_dev",
                    "firstName": "Juan",
                    "lastName": "Pérez",
                    "phone": "6641234567",
                    "email": "juan4#gmail.com",
                    "age": 25
                }""";

        // TODO: realizar POST /users con el body anterior
        // TODO: andExpect status 201
        // TODO: andExpect jsonPath("$.info") contiene "creado" o similar
        // TODO: andExpect jsonPath("$.response.user.username") == "juan4_dev"
        // TODO: andExpect jsonPath("$.response.user.status") == "ACTIVE"
    }

    @Test
    void shouldReturn400WhenUsernameIsTooShort() throws Exception {
        // TODO: body con username de 4 caracteres
        // TODO: realizar POST /users
        // TODO: andExpect status 400
            String body = """
                {"username":"ab4",
                "firstName":"Mayrin",
                "lastName":"Peredia",
                "phone":"6461234567",
                "email":"mayrin4#hola.com",
                "age":20 }""";
            mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isBadRequest());

    }

    @Test
    void shouldReturn400WhenAgeIsExactlyTwelve() throws Exception {
        // TODO: body con age = 12 (caso límite — debe ser mayor a 12)
        // TODO: realizar POST /users
        // TODO: andExpect status 400
        String body = """
                {"username":"abcd4",
                "firstName":"Mayrin",
                "lastName":"Peredia",
                "phone":"6461234567",
                "email":"mayrin4#hola.com",
                "age":12 }""";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenPhoneIsInvalid() throws Exception {
        // TODO: body con phone = "123" (menos de 10 dígitos)
        // TODO: realizar POST /users
        // TODO: andExpect status 400
        String body = """
                {"username":"abcd4",
                "firstName":"Mayrin",
                "lastName":"Peredia",
                "phone":"123",
                "email":"mayrin4#hola.com",
                "age":20 }""";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenEmailIsInvalid() throws Exception {
        // TODO: body con email en formato estándar "user@gmail.com" (no cumple las reglas del validador)
        // TODO: realizar POST /users
        // TODO: andExpect status 400

        String body = """
                {"username":"abcd4",
                "firstName":"Mayrin",
                "lastName":"Peredia",
                "phone":"6461234567",
                "email":"mayrin4#gmail.com",
                "age":20 }""";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenUsernameIsDuplicated() throws Exception {
        // TODO: guardar un usuario directamente via repository con el mismo username
        // TODO: realizar segundo POST /users con el mismo username
        // TODO: andExpect status 409
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valid_body))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/users")
                .contentType(valid_body))
                .andExpect(status().isConflict());
    }

    // ─── GET /users/{id} ─────────────────────────────────────────────────────

    @Test
    void shouldReturn200AndUserWhenFound() throws Exception {
        User saved = userRepository.save( new User("juan4_dev",
                "Juan",
                "Pérez",
                "6641234567",
                "juan4#gmail.com", 25)
        );

        mockMvc.perform(get("/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.user.username").value("juan4_dev"));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        User saved = userRepository.save( new User("juan4_dev",
                "Juan",
                "Pérez",
                "6641234567",
                "juan4#gmail.com", 25)
        );

        mockMvc.perform(get("/user/9999"))
                .andExpect(status().isNotFound());
    }

    // ─── PATCH /users/{id}/suspend ────────────────────────────────────────────

    @Test
    void shouldSuspendUserAndReturn200() throws Exception {
        // TODO: guardar un usuario ACTIVE via repository
        // TODO: realizar PATCH /users/{id}/suspend
        // TODO: andExpect status 200
        // TODO: andExpect jsonPath("$.response.user.status") == "SUSPENDED"

        User saved = userRepository.save(new User("juan4_dev","Juan","Pérez","6641234567","juan4#gmail.com",25));
        mockMvc.perform(patch("/users/" + saved.getId() +"/suspend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.user.status").value("SUSPENDED"));
    }

    @Test
    void shouldReturn400WhenSuspendingAlreadySuspendedUser() throws Exception {
        // TODO: guardar un usuario con status SUSPENDED via repository
        // TODO: realizar PATCH /users/{id}/suspend
        // TODO: andExpect status 400
        User user = new User("juan4_dev","Juan","Pérez","6641234567","juan4#gmail.com",25);

    user.setStatus(UserStatus.SUSPENDED);

    User saved = userRepository.save(user);
    mockMvc.perform(patch("/users/" + saved.getId() + "/suspend"))
            .andExpect(status().isBadRequest());
    }
}
