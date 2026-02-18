package hexlet.code.util;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Утилитарный компонент для работы с данными текущего аутентифицированного пользователя.
 * Предоставляет методы для получения сведений о пользователе из SecurityContext.
 */
@Component
@RequiredArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;


    /**
     * Получает сведения о текущем аутентифицированном пользователе из SecurityContext.
     * Извлекает email из объекта Authentication и находит соответствующего пользователя в базе данных.
     *
     * @return объект {@link User}, представляющий текущего аутентифицированного пользователя
     * @throws ResourceNotFoundException если пользователь не аутентифицирован
     * или пользователь с указанным email не найден в базе данных
     */
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
