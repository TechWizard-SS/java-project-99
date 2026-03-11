package hexlet.code.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

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
     * Обрабатывает любые неожиданные {@link Exception}, не перехваченные другими обработчиками.
     * Возвращает ответ с кодом состояния HTTP 500 (INTERNAL_SERVER_ERROR)
     * и телом JSON, содержащим обобщённое сообщение об ошибке.
     *
     * @param ex исключение {@link Exception}, возникшее в приложении
     * @return {@link ResponseEntity} с HTTP статусом 500 и обобщённым сообщением об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error"));
    }

    /**
     * Обрабатывает исключение {@link DuplicateResourceException}.
     * Возвращает ответ с кодом состояния HTTP 409 (CONFLICT)
     * и телом JSON, содержащим сообщение об ошибке из исключения.
     *
     * @param ex исключение {@link DuplicateResourceException}, возникшее в приложении
     * @return {@link ResponseEntity} с HTTP статусом 409 и сообщением об ошибке
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateResource(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * Обрабатывает исключение {@link ResponseStatusException}.
     * Возвращает ответ с кодом состояния и сообщением, указанными в исключении.
     * Если сообщение (reason) в исключении равно null, используется резервное сообщение "Authentication failed".
     *
     * @param ex исключение {@link ResponseStatusException}, возникшее в приложении
     * @return {@link ResponseEntity} с HTTP статусом из исключения и сообщением об ошибке
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("error", ex.getReason() != null ? ex.getReason() : "Authentication failed"));
    }


    /**
     * Обрабатывает исключение {@link DataIntegrityViolationException}.
     * Это исключение обычно возникает при нарушении ограничений целостности данных в базе,
     * например, при попытке удалить сущность, на которую ссылаются другие сущности.
     * Возвращает ответ с кодом состояния HTTP 409 (CONFLICT)
     * и телом JSON, содержащим сообщение об ошибке.
     *
     * @param ex исключение {@link DataIntegrityViolationException}, возникшее в приложении
     * @return {@link ResponseEntity} с HTTP статусом 409 и сообщением об ошибке целостности данных
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Cannot delete resource: it is currently in use"));
    }
}
