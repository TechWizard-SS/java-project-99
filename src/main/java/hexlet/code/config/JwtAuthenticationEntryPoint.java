package hexlet.code.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Компонент, реализующий интерфейс {@link AuthenticationEntryPoint}.
 * Используется Spring Security для обработки ситуаций, когда неаутентифицированный
 * пользователь пытается получить доступ к защищённому ресурсу.
 * Отправляет клиенту ответ с кодом состояния HTTP 401 (Unauthorized)
 * и телом в формате JSON, содержащим сообщение об ошибке.
 */
@Component
public final class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Метод вызывается, когда Spring Security определяет, что запрос требует аутентификации,
     * но пользователь не аутентифицирован.
     * Устанавливает тип содержимого ответа в 'application/json',
     * статус ответа в 401 (SC_UNAUTHORIZED) и записывает сообщение об ошибке
     * в тело ответа в формате JSON.
     *
     * @param request       объект {@link HttpServletRequest}, представляющий входящий HTTP-запрос
     * @param response      объект {@link HttpServletResponse}, представляющий исходящий HTTP-ответ
     * @param authException исключение {@link AuthenticationException}, возникшее при попытке аутентификации
     * @throws IOException если произошла ошибка ввода-вывода при записи ответа
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Unauthorized: " + authException.getMessage() + "\"}");
    }
}
