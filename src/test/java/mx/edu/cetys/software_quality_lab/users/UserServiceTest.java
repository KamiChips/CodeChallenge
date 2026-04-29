package mx.edu.cetys.software_quality_lab.users;

import mx.edu.cetys.software_quality_lab.users.exceptions.DuplicateUsernameException;
import mx.edu.cetys.software_quality_lab.users.exceptions.InvalidUserDataException;
import mx.edu.cetys.software_quality_lab.users.exceptions.UserNotFoundException;
import mx.edu.cetys.software_quality_lab.validators.EmailValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    // EmailValidatorService debe ser mockeado — en pruebas unitarias no probamos dependencias externas
    @Mock
    EmailValidatorService emailValidatorService;

    @InjectMocks
    UserService userService;

    // ─── Caso exitoso ─────────────────────────────────────────────────────────
    private UserController.UserRequest validRequest(){
        return new UserController.UserRequest("Mayrin4_dev", "Mayrin", "Peredia","6462526769", "mayrin4#mirame.com", 20);
    }

    private User userWithId(Long id){
        User u = new User("Mayrin4_dev", "Mayrin", "Peredia","6462526769", "mayrin4#mirame.com", 20);
        u.setId(id);
        return u;
    }
    @Test
    void shouldRegisterUserSuccessfully() {
        when(emailValidatorService.isValid(anyString()))
                .thenReturn(true);
        when(userRepository.existsByUsername("Mayrin4_dev"))
                .thenReturn(true);
        when(userRepository.save(any(User.class)))
                .thenReturn(userWithId(1L));
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        // TODO: arrange — mockear userRepository.findById para que regrese un Optional<User> con datos
        // TODO: act — llamar a userService.getUserById(1L)
        // TODO: assert — verificar que los campos del response coincidan con el mock
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(userWithId(1L)));

        var response = userService.getUserById(1L);

        assertEquals(1L, response.id());
        assertEquals("Mayrin4_dev", response.username());
    }

    @Test
    void shouldSuspendActiveUserSuccessfully() {
        // TODO: arrange — mockear findById con un usuario ACTIVE
        // TODO: act — llamar a userService.suspendUser(id)
        // TODO: assert — verificar que el status regresado sea "SUSPENDED"; confirmar que save fue llamado
        User activeUser = userWithId(1L);
        activeUser.setStatus(UserStatus.ACTIVE);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(activeUser));

        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var response = userService.suspendUser(1L);

        assertEquals("SUSPENDED", response.status());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ─── Validaciones de Username ─────────────────────────────────────────────

    @Test
    void shouldThrowWhenUsernameTooShort() {
        // TODO: construir request con username de 4 caracteres
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("abc4", "Mayrin", "Peredia", "6461234567", "mayrin4#mirame.com",20);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    @Test
    void shouldThrowWhenUsernameTooLong() {
        // TODO: construir request con username de 21 caracteres
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("abh6cjam2jsli8acntop1", "Mayrin", "Peredia", "6461234567", "mayrin4#mirame.com",20);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));

    }

    @Test
    void shouldThrowWhenUsernameHasInvalidChars() {
        // TODO: username con mayúsculas o caracteres especiales, ej. "User@Name"
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenUsernameStartsWithUnderscore() {
        // TODO: username "_nombrevalido"
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("_Mayrin4", "Mayrin", "Peredia", "6461234567", "mayrin4#mirame.com",20);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    @Test
    void shouldThrowWhenUsernameEndsWithUnderscore() {
        // TODO: username "nombrevalido_"
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("Mayrin4_", "Mayrin", "Peredia", "6461234567", "mayrin4#mirame.com",20);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    // ─── Validaciones de Nombre ───────────────────────────────────────────────

    @Test
    void shouldThrowWhenFirstNameTooShort() {
        // TODO: firstName de 1 carácter
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("Mayrin4_dev", "M", "Peredia","6462526769", "mayrin4#mirame.com", 20);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    @Test
    void shouldThrowWhenFirstNameContainsNumbers() {
        // TODO: firstName como "Juan5"
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("Mayrin4_dev", "Juan5", "Peredia","6462526769", "mayrin4#mirame.com", 20);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    @Test
    void shouldThrowWhenLastNameTooShort() {
        // TODO: lastName de 1 carácter
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("Mayrin4_dev", "Mayrin", "P","6462526769", "mayrin4#mirame.com", 20);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    @Test
    void shouldThrowWhenLastNameContainsNumbers() {
        // TODO: lastName como "Perez2"
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("Mayrin4_dev", "Mayrin", "Perez2","6462526769", "mayrin4#mirame.com", 20);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    // ─── Validaciones de Age ─────────────────────────────────────────────────

    @Test
    void shouldThrowWhenAgeIsExactlyTwelve() {
        // TODO: age = 12 — caso límite (boundary): debe ser MAYOR a 12, no menor o igual
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("Mayrin4_dev", "Mayrin", "Peredia","6462526769", "mayrin4#mirame.com", 12);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    @Test
    void shouldThrowWhenAgeIsBelowTwelve() {
        // TODO: age = 5
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("Mayrin4_dev", "Mayrin", "Peredia","6462526769", "mayrin4#mirame.com", 5);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    @Test
    void shouldThrowWhenAgeExceedsMaximum() {
        // TODO: age = 121 — excede el máximo permitido de 120
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("Mayrin4_dev", "Mayrin", "Peredia","6462526769", "mayrin4#mirame.com", 121);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    // ─── Validaciones de Phone ───────────────────────────────────────────────

    @Test
    void shouldThrowWhenPhoneHasWrongLength() {
        // TODO: phone con 9 u 11 dígitos
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("Mayrin4_dev", "Mayrin", "Peredia","64625267691", "mayrin4#mirame.com", 20);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    @Test
    void shouldThrowWhenPhoneContainsLetters() {
        // TODO: phone como "123456789a"
        // TODO: assertThrows InvalidUserDataException
        var user = new UserController.UserRequest("Mayrin4_dev", "Mayrin", "Peredia","646252676a", "mayrin4#mirame.com", 20);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
    }

    // ─── Validación de Email ──────────────────────────────────────────────────

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        // TODO: mockear emailValidatorService.isValid(anyString()) para que regrese false
        // TODO: assertThrows InvalidUserDataException
        // TODO: verificar que emailValidatorService.isValid fue llamado (verify)
        when(emailValidatorService.isValid(anyString()))
                .thenReturn(false);
        var user = new UserController.UserRequest("Mayrin4_dev", "Mayrin", "Peredia","6462526769", "mayrin4#mirame.com", 20);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(user));
        verify(emailValidatorService,times(1)).isValid(anyString());
    }

    // ─── Unicidad de Username ─────────────────────────────────────────────────

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        // TODO: mockear emailValidatorService.isValid para que regrese true
        // TODO: mockear userRepository.existsByUsername para que regrese true
        // TODO: assertThrows DuplicateUsernameException
        // TODO: verificar que userRepository.save NUNCA fue llamado (verify never)
    }

    // ─── Not found ───────────────────────────────────────────────────────────

    @Test
    void shouldThrowWhenUserNotFound() {
        // TODO: mockear userRepository.findById para que regrese Optional.empty()
        // TODO: assertThrows UserNotFoundException
    }

    @Test
    void shouldThrowWhenSuspendingAlreadySuspendedUser() {
        // TODO: mockear findById con un usuario SUSPENDED
        // TODO: assertThrows InvalidUserDataException
    }
}
