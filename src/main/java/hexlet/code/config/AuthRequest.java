package hexlet.code.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс-сущность (DTO) для передачи данных аутентификации (имя пользователя и пароль)
 * от клиента к серверу при попытке входа в систему.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthRequest {

    /**
     * Имя пользователя (в данном контексте, email).
     */
    private String username;

    /**
     * Пароль пользователя.
     */
    private String password;
}
