package hexlet.code.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;

/**
 * Глобальный обработчик исключений для контроллеров приложения.
 * Перехватывает и обрабатывает стандартные типы исключений,
 * возвращая соответствующие HTTP-статусы и сообщения об ошибках в формате JSON.
 */
@ControllerAdvice
public final class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение {@link ResourceNotFoundException}.
     * Возвращает ответ с кодом состояния HTTP 404 (NOT FOUND)
     * и телом JSON, содержащим сообщение об ошибке.
     *
     * @param ex исключение {@link ResourceNotFoundException}, возникшее в приложении
     * @return {@link ResponseEntity} с HTTP статусом 404 и сообщением об ошибке
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    /**
     * Обрабатывает исключение {@link MethodArgumentNotValidException}.
     * Это исключение возникает при ошибках валидации входящих данных (@Valid).
     * Возвращает ответ с кодом состояния HTTP 400 (BAD REQUEST)
     * и телом JSON, содержащим список ошибок валидации.
     *
     * @param ex исключение {@link MethodArgumentNotValidException}, возникшее при валидации
     * @return {@link ResponseEntity} с HTTP статусом 400 и списком ошибок валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errors", errors));
    }

    /**
     * Обрабатывает исключение {@link AccessDeniedException}.
     * Возвращает ответ с кодом состояния HTTP 403 (FORBIDDEN)
     * и телом JSON, содержащим сообщение об ошибке доступа.
     *
     * @param ex исключение {@link AccessDeniedException}, возникшее при попытке доступа
     * @return {@link ResponseEntity} с HTTP статусом 403 и сообщением об ошибке доступа
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied: " + ex.getMessage()));
    }

    /**
     * Обрабатывает исключение {@link RuntimeException}.
     * Используется для перехвата других стандартных {@link RuntimeException},
     * например, выбрасываемых в сервисах при нарушении уникальности (email, slug).
     * Возвращает ответ с кодом состояния HTTP 400 (BAD REQUEST)
     * и телом JSON, содержащим сообщение об ошибке.
     *
     * @param ex исключение {@link RuntimeException}, возникшее в приложении
     * @return {@link ResponseEntity} с HTTP статусом 400 и сообщением об ошибке
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}
