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

#### Obtener usuario por ID
#### Archivos
**UserService** Se implemento la logica de busqueda de un usuario por ID con 'findByID'.
Si el resultado esta vacio lanza 'UserNotFoundException', si existe mapea la entidad a un 'UserResponse' y lo devulve.
**UserController** Se implemento el endpoint 'GET /user/{id}' que recibe el ID como '@PathVariable', llama al servicio y envuelve el resultado en un 'APIResponse'.
**UserControllerAdvice** Se implemento el manejo de 'UserNotFoundException' y regresa un HTTP 404 con el mensaje de error dento de 'APIRespnse'.

#### Tests
**UserServiceTest**
- shouldGetUserByIdSuccessfully(): Mockea 'findById' con un usuario valido y verifica que el response tenga los camposcorrectos.
- shouldThrowWhenUserNotFound(): Mockea 'findById' retornando 'Optional.empty()' y verifica que se lance 'UserNotFoundException'.

**UserControllerIntegrationTest**
- shouldReturn200AndUserWhenFound(): Guarda un usuario en H2 y verifica que 'GET /users/{id}' regrese 200 con los datos correctos.
- shouldReturn404WhenUserNotFound(): Llama a 'GET /users/{id}' y verifica que regrese 404.

### Cobertura obtenida

| Métrica | Resultado |
|---|---|
| Line coverage | <!-- ej. 91% --> |
| Branch coverage | <!-- ej. 87% --> |

---

## Challenge Extra — Módulo `petstore`

### ¿Qué implementaron?


<!-- Describan qué lograron del challenge, si lo completaron o hasta dónde llegaron -->

### Cobertura obtenida

| Métrica | Resultado |
|---|---|
| Line coverage | <!-- ej. 88% --> |
| Branch coverage | <!-- ej. 85% --> |
