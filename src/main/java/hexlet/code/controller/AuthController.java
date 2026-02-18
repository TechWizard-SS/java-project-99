package hexlet.code.controller;

import hexlet.code.config.AuthRequest;
import hexlet.code.config.JwtUtil;
import hexlet.code.config.MyUserDetailsService;
import hexlet.code.util.NamedRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Контроллер, отвечающий за аутентификацию пользователей.
 * Обрабатывает запросы на получение JWT-токена.
 */
@RestController
@RequestMapping(NamedRoutes.API)
@RequiredArgsConstructor
public final class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MyUserDetailsService userDetailsService;

    /**
     * Обрабатывает POST-запрос на аутентификацию пользователя.
     * Принимает имя пользователя (email) и пароль, проверяет их,
     * и если данные верны, генерирует и возвращает JWT-токен.
     *
     * @param authRequest объект {@link AuthRequest}, содержащий имя пользователя и пароль
     * @return JWT-токен в виде строки, если аутентификация прошла успешно
     * @throws ResponseStatusException с кодом 401 (UNAUTHORIZED), если предоставлены неверные учетные данные
     */
    @PostMapping(NamedRoutes.LOGIN)
    public String login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            // Пытаемся аутентифицировать пользователя с помощью AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // Если аутентификация не удалась (неверный логин/пароль),
            // выбрасываем исключение с кодом 401
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Если аутентификация прошла успешно, загружаем детали пользователя
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // Генерируем JWT-токен для аутентифицированного пользователя
        final String jwt = jwtUtil.generateToken(userDetails);

        // Возвращаем токен.
        return jwt;
    }
}
