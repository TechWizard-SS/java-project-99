package hexlet.code.exception;

/**
 * Исключение, которое выбрасывается, когда запрашиваемый ресурс
 * (например, пользователь, задача, статус) не найден в системе.
 * Наследуется от {@link RuntimeException}, что делает его непроверяемым.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Конструктор исключения с сообщением об ошибке.
     *
     * @param message подробное сообщение об ошибке, объясняющее причину исключения
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
