package hexlet.code.util;

/**
 * Класс, содержащий именованные константы для часто используемых маршрутов API.
 * Предназначен для централизованного хранения URL-путей, что облегчает рефакторинг
 * и уменьшает количество жёстко закодированных строк в контроллерах и тестах.
 * Также предоставляет вспомогательные методы для построения полных путей с идентификаторами.
 */
public final class NamedRoutes {

    // Базовые пути
    public static final String API = "/api";

    // Пользователи
    public static final String USERS = API + "/users";
    public static final String USER_ID = "/{id}";
    public static final String LOGIN = "/login";

    // Статусы задач
    public static final String TASK_STATUSES = API + "/task_statuses";
    public static final String TASK_STATUS_ID = "/{id}";
    public static final String TASK_STATUS_SLUG = "/slug/{slug}";

    // Метки (Labels)
    public static final String LABELS = API + "/labels";
    public static final String LABEL_ID = "/{id}";

    // Задачи (Tasks)
    public static final String TASKS = API + "/tasks";
    public static final String TASK_ID = "/{id}";

    // Методы для построения путей (полезно для тестов или редиректов)
    public static String userPath(Long id) {
        return USERS + "/" + id;
    }

    public static String taskPath(Long id) {
        return TASKS + "/" + id;
    }

    public static String labelPath(Long id) {
        return LABELS + "/" + id;
    }

    public static String taskStatusPath(Long id) {
        return TASK_STATUSES + "/" + id;
    }
}
