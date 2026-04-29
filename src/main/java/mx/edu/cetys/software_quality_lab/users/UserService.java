package mx.edu.cetys.software_quality_lab.users;

import mx.edu.cetys.software_quality_lab.users.exceptions.DuplicateUsernameException;
import mx.edu.cetys.software_quality_lab.users.exceptions.InvalidUserDataException;
import mx.edu.cetys.software_quality_lab.users.exceptions.UserNotFoundException;
import mx.edu.cetys.software_quality_lab.validators.EmailValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final EmailValidatorService emailValidatorService;

    public UserService(UserRepository userRepository, EmailValidatorService emailValidatorService) {
        this.userRepository = userRepository;
        this.emailValidatorService = emailValidatorService;
    }

    /**
     * Registrar un nuevo usuario aplicando todas las reglas de negocio.
     *
     * Reglas a implementar (lanzar InvalidUserDataException a menos que se indique):
     *  1. Username  — entre 5 y 20 caracteres, solo letras minúsculas, dígitos y guion bajo (_),
     *                 NO debe comenzar ni terminar con guion bajo --YA--
     *  2. First name — entre 2 y 50 caracteres, solo letras (se permiten acentos: á, é, ñ, etc.) --YA--
     *  3. Last name  — entre 2 y 50 caracteres, solo letras (se permiten acentos) --YA--
     *  4. Age        — debe ser mayor a 12 y menor o igual a 120 --YA--
     *  5. Phone      — exactamente 10 dígitos, sin letras ni símbolos --YA--
     *  6. Email      — delegar a emailValidatorService.isValid(email); --YA--
     *                  lanzar InvalidUserDataException si regresa false
     *  7. Unicidad del username — si userRepository.existsByUsername regresa true,
     *                             lanzar DuplicateUsernameException
     */
    UserController.UserResponse registerUser(UserController.UserRequest request) {
        log.info("Iniciando registro de usuario, username={}", request.username());
        String username = request.username();

        if(username == null || username.length() < 5 || username.length() > 20){
            throw new InvalidUserDataException("The username must be between 5 - 20 characters");
        }
        if(!username.matches("[a-z0-9_]+")){
            throw new InvalidUserDataException("The username can contain lower case, 0 - 9 and _");
        }

        if(username.startsWith("_") || username.endsWith("_")){
            throw new InvalidUserDataException("The user must not start or end with \"_\" ");
        }

        String firstName = request.firstName();
        if(firstName == null || firstName.length() < 2 || firstName.length() > 50){
            throw new InvalidUserDataException("First name must be between 2 and 50 characters");
        }

        if(!firstName.matches("[^[\\\\p{L}áéíóúÁÉÍÓÚñÑ]+$]")){
            throw new InvalidUserDataException("First name must only contain letters");
        }

        String lastName = request.lastName();
        if(lastName == null || lastName.length() < 2 || lastName.length() > 50){
            throw new InvalidUserDataException("Last name must be between 2 and 50 characters");
        }

        if(!lastName.matches("[^[\\\\p{L}áéíóúÁÉÍÓÚñÑ]+$]")){
            throw new InvalidUserDataException("Last name must only contain letters");
        }

        String phone = request.phone();
        if(phone == null || !phone.matches("\\d{10}")){
            throw new InvalidUserDataException("The phone must be 10 numbers");
        }

        Integer age = request.age();
        if(age == null || age < 12 || age >= 120){
            throw new InvalidUserDataException("User's age must be between 12 and 120 ");
        }

        if(!emailValidatorService.isValid(request.email())){
            throw new InvalidUserDataException("The email is invalid");
        }

        if(userRepository.existsByUsername(username)){
            throw new DuplicateUsernameException("El username "+ username + " ya esta registrado");
        }

        var save = userRepository.save(new User(username,firstName, lastName,phone, request.email(), age));

        log.info("User registered succesfully, id={}", save.getId());
        return mapToResponse(save);
    }

    /**
     * Buscar un usuario por ID.
     * Lanzar UserNotFoundException (HTTP 404) si el usuario no existe.
     * Mayrin
     */
    UserController.UserResponse getUserById(Long id) {
        log.info("Buscando usuario por ID, id={}", id);
        // TODO: buscar por id con findById, lanzar UserNotFoundException si está vacío, mapear y regresar
        var userID =  userRepository.findById(id);
        if (userID.isEmpty()) {
            throw new UserNotFoundException("Usuario no encontrado con id=" + id);
        }
        return mapToResponse(userID.get());
    }

    /**
     * Suspender un usuario ACTIVO.
     * Lanzar UserNotFoundException si el usuario no existe.
     * Lanzar InvalidUserDataException si el usuario ya está SUSPENDED.
     */
    UserController.UserResponse suspendUser(Long id) {
        log.info("Suspendiendo usuario, id={}", id);
        // TODO: buscar usuario, validar status, cambiar a SUSPENDED, guardar, mapear y regresar

        var userFromDb = userRepository.findById(id);
        if (userFromDb.isEmpty()){
            throw new
                    UserNotFoundException("Usuario con id " + id + "no encontrado");
        }
        var user = userFromDb.get();
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new
                    InvalidUserDataException("El usuario ya esta suspendido");
        }

        user.setStatus(UserStatus.SUSPENDED);
        var saved = userRepository.save(user);
        log.info("Usuario Suspendido exitosamenre, id ={}", saved.getId());
        return mapToResponse(saved);
    }

    private UserController.UserResponse mapToResponse(User user) {
        return new UserController.UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getEmail(),
                user.getAge(),
                user.getStatus().name()
        );
    }
}
