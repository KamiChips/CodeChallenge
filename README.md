# Code Challenge — Laboratorio de Pruebas de Software

## Equipo

**Nombre del equipo:** Parangaricutirimicuaro

| Nombre completo               | Matrícula |
|-------------------------------|-----------|
| Martha Kamila Santos Maciel   | 12708     |
| Cynthia Mayrin Peredia Parra  | 12805     |
| Christopher Dael Sandez Curro | 13969     |

---

## Entrega

**Fecha:** <!-- Fecha de entrega --> 28/04/2026

**Modalidad:** <!-- ZIP / GitHub --> Presencial

**Link del repositorio (si aplica):** <!-- https://github.com/... --> https://github.com/KamiChips/CodeChallenge.git

---

## Parte 1 — Módulo `users`

### ¿Qué implementaron?

<!-- Describan brevemente qué hicieron, dificultades que encontraron o decisiones que tomaron -->

**Kamila**
## Archivos
Dentro del archivo de **UserService** agregue la lógica de las reglas que se deben de seguir para que se cree un usuario valido.
Cada regla se tomo en cuenta y se implemento, se verifico que no hiciera falta ninguna regla.
Posteriormente dentro de **UserController** se modifico la respuesta con el envoltorio de ApiResponse.
Dentro de **UserControllerAdvice** solo se modifico el tipo de respuesta que se entregaria en caso de una mala request.
Por ultimo, se agregaron las pruebas unitarias y de integración, en donde se cubrieron la mayoría de las branches de coverage dentro del código,
se implementaron pruebas para usuarios de longitud corta y larga, asi como pruebas de numero de celular si era menor a 10 numero, etc.

## Dificultades
Dentro de un momento de presion accidentalmente empece a subir archivos de gradle, build, etc., y en un intento desesperado de revertir el push 
de todo esos archivos accidentalmente elimine todo el progreso de usuarios que habia realizado, tomando en cuenta que tambien afectaria a mi equipo, ademas 
de que en su momento hacia falta mis pruebas de integracion. Finalmente, se pudieron recuperar los archivos, pero esta vez ya no eran trackeados por git, por
lo que tuve que forzar la eliminacion de cache de los archivos en git y volver a subir todo.

#### Obtener usuario por ID - Mayrin
#### Archivos
**UserService** Se implemento la logica de busqueda de un usuario por ID con `findByID`'.
Si el resultado esta vacio lanza `UserNotFoundException`, si existe mapea la entidad a un `UserResponse` y lo devulve.
**UserController** Se implemento el endpoint `GET /user/{id}` que recibe el ID como `@PathVariable`, llama al servicio y envuelve el resultado en un 'APIResponse'.
**UserControllerAdvice** Se implemento el manejo de 'UserNotFoundException' y regresa un HTTP 404 con el mensaje de error dento de `APIRespnse`.

#### Tests
**UserServiceTest**
- shouldGetUserByIdSuccessfully(): Mockea `findById` con un usuario valido y verifica que el response tenga los camposcorrectos.
- shouldThrowWhenUserNotFound(): Mockea `findById`retornando `Optional.empty()` y verifica que se lance `UserNotFoundException`.

**UserControllerIntegrationTest**
- shouldReturn200AndUserWhenFound(): Guarda un usuario en H2 y verifica que `GET /users/{id}` regrese 200 con los datos correctos.
- shouldReturn404WhenUserNotFound(): Llama a `GET /users/{id}` y verifica que regrese 404.

#### Suspender usuario - Christopher

**Archivos**
* **UserService:** Se implementó la lógica para cambiar el estatus de un usuario a suspendido. Se incluyeron las validaciones de negocio correspondientes: si el usuario no es encontrado se lanza `UserNotFoundException`, y si el usuario ya se encuentra con estatus de `SUSPENDED`, se lanza un `InvalidUserDataException`. Si pasa las validaciones, se actualiza el estado y se persiste en la base de datos.
* **UserController:** Se implementó el endpoint `PATCH /users/{id}/suspend` que recibe el ID del usuario mediante `@PathVariable`, ejecuta la lógica del servicio y retorna el usuario actualizado envuelto en un `ApiResponse` con un código HTTP 200.
* **UserControllerAdvice:** Se completó la implementación del método `handleInvalidUserData`. Se eliminó la excepción temporal (`UnsupportedOperationException`) y se configuró para que retorne correctamente un HTTP 400 (Bad Request) con el mensaje de error cuando fallan las reglas de negocio.

**Tests**
* **UserServiceTest:** Se implementaron pruebas unitarias para asegurar que el servicio cambie el estado exitosamente, así como para verificar que se disparen las excepciones correctas (usuario inexistente o ya suspendido).
* **UserControllerIntegrationTest:** Se añadieron pruebas con `MockMvc` para simular las peticiones `PATCH`. Específicamente, se comprobó que se retorne el estatus 200 en un caso de éxito y el estatus 400 (`isBadRequest()`) al intentar suspender a un usuario ya suspendido.

**Dificultades**
* Al correr las pruebas de integración, el test que verificaba la suspensión de un usuario ya suspendido fallaba. El test esperaba un código 400, pero la aplicación devolvía un 500. Al realizar el proceso de *debugging* en los logs, detecté que el servicio lanzaba la excepción correctamente, pero el manejador global (`UserControllerAdvice`) tenía un `TODO` sin implementar que hacía *crash* en la aplicación. Reemplazar ese código por el mapeo correcto del `ApiResponse` solucionó el problema.

### Cobertura obtenida

| Métrica | Resultado |
|---|---|
| Line coverage | <!-- ej. 91% --> |
| Branch coverage | <!-- ej. 87% --> |

---

## Challenge Extra — Módulo `petstore`

### ¿Qué implementaron?
#### Mayrin

#### Archivos
**createAdoption** Valida 6 reglas antes de persistir
En caso de éxito marca 'pet.available = false' y guarda la adopción.
**cancelAdoption** Valida 2 reglas
En caso de éxito cambia el status a `CANCELLED` y devuelve `pet.available = true`.
**listAvailablePets** Consulta todos los pets con `available = true` y los regresa como lista.


<!-- Describan qué lograron del challenge, si lo completaron o hasta dónde llegaron -->

### Cobertura obtenida

| Métrica | Resultado |
|---|---|
| Line coverage | <!-- ej. 88% --> |
| Branch coverage | <!-- ej. 85% --> |
