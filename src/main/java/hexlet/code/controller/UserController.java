package hexlet.code.controller;

import hexlet.code.model.dto.User.UserCreateDTO;
import hexlet.code.model.dto.User.UserDTO;
import hexlet.code.model.dto.User.UserUpdateDTO;
import hexlet.code.service.UserService;
import hexlet.code.util.NamedRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.lang.Exception;import io.sentry.Sentry;
/**
 * Контроллер для управления пользователями ({@link hexlet.code.model.User}).
 * Обрабатывает HTTP-запросы для получения списка пользователей, просмотра,
 * создания, обновления и удаления отдельных пользователей.
 * Требует аутентификации для операций обновления и удаления.
 */
@RestController
@RequestMapping(NamedRoutes.USERS)
@RequiredArgsConstructor
public final class UserController {

    private final UserService userService;

    /**
     * Обрабатывает GET-запрос на получение списка всех пользователей.
     * Возвращает список DTO пользователей и заголовок X-Total-Count.
     * Этот маршрут, вероятно, доступен всем.
     *
     * @return {@link ResponseEntity} с HTTP статусом 200 OK и телом, содержащим список {@link UserDTO}
     */
    @GetMapping("")
    public ResponseEntity<List<UserDTO>> index() {
        List<UserDTO> users = userService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    /**
     * Обрабатывает GET-запрос на получение пользователя по его идентификатору.
     * Этот маршрут, вероятно, доступен всем.
     *
     * @param id идентификатор пользователя
     * @return DTO найденного пользователя {@link UserDTO}
     */
    @GetMapping(NamedRoutes.USER_ID)
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable Long id) {
        return userService.findById(id);
    }

    /**
     * Обрабатывает POST-запрос на создание нового пользователя.
     * Этот маршрут, вероятно, доступен всем.
     * Принимает DTO с данными для создания.
     *
     * @param userData DTO {@link UserCreateDTO} с данными нового пользователя
     * @return DTO созданного пользователя {@link UserDTO}
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO userData) {
        return userService.create(userData);
    }

    /**
     * Обрабатывает PUT-запрос на обновление существующего пользователя.
     * Требует аутентификации. Пользователь может обновлять только свои данные.
     * Принимает DTO с новыми данными и идентификатор пользователя.
     * Примечание: В текущей реализации {@code currentUser} не используется в вызове сервиса.
     * Логика проверки доступа должна быть реализована в {@link UserService#update}.
     *
     * @param userData    DTO {@link UserUpdateDTO} с новыми данными пользователя
     * @param id          идентификатор обновляемого пользователя
     * @param currentUser объект {@link UserDetails}, представляющий аутентифицированного пользователя
     * @return DTO обновлённого пользователя {@link UserDTO}
     */
    @PutMapping(NamedRoutes.USER_ID)
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@Valid @RequestBody UserUpdateDTO userData,
                          @PathVariable Long id,
                          @AuthenticationPrincipal UserDetails currentUser) {

        return userService.update(userData, id);
    }

    /**
     * Обрабатывает DELETE-запрос на удаление пользователя по его идентификатору.
     * Требует аутентификации. Пользователь может удалять только свой аккаунт.
     * Примечание: В текущей реализации {@code currentUser} не используется в вызове сервиса.
     * Логика проверки доступа должна быть реализована в {@link UserService#delete}.
     *
     * @param id          идентификатор удаляемого пользователя
     * @param currentUser объект {@link UserDetails}, представляющий аутентифицированного пользователя
     * @throws ResponseStatusException с кодом 401 (UNAUTHORIZED), если пользователь не аутентифицирован
     */
    @DeleteMapping(NamedRoutes.USER_ID)
    @ResponseStatus(HttpStatus.OK)
    public void destroy(@PathVariable Long id,
                        @AuthenticationPrincipal UserDetails currentUser) {

        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        try {
            throw new Exception("Sentry Test: Удаление пользователя ID " + id);
        } catch (Exception e) {
            Sentry.captureException(e);
        }

        userService.delete(id);
    }
}
