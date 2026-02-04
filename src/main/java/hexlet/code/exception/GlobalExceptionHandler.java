package hexlet.code.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

/**
 * Global exception handler for the application.
 */
@ControllerAdvice
public final class GlobalExceptionHandler {

  /**
   * Handles resource not found exceptions.
   *
   * @param ex resource not found exception
   * @return response entity with error message and 404 status
   */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

  /**
   * Handles access denied exceptions.
   *
   * @param ex access denied exception
   * @return response entity with error message and 403 status
   */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied: " + ex.getMessage()));
    }
}
