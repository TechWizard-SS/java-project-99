package hexlet.code.component;

import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.repository.TaskRepository;
import hexlet.code.util.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Утилитарный компонент для проверки прав доступа (авторизации) пользователей.
 * Предоставляет методы для проверки, является ли текущий пользователь
 * автором задачи или владельцем профиля.
 */
@Component("authorizer")
@RequiredArgsConstructor
public class Authorizer {

    private final TaskRepository taskRepository;
    private final UserUtils userUtils;

    /**
     * Проверяет, является ли текущий аутентифицированный пользователь
     * назначенным исполнителем (assignee) задачи с указанным идентификатором.
     *
     * @param taskId идентификатор задачи для проверки
     * @return true, если текущий пользователь является исполнителем задачи, иначе false
     * @throws ResourceNotFoundException если задача с указанным идентификатором не найдена
     */
    public final boolean isAuthor(Long taskId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        var currentUser = userUtils.getCurrentUser();
        return task.getAssignee().getId().equals(currentUser.getId());
    }

    /**
     * Проверяет, соответствует ли указанный идентификатор пользователя
     * идентификатору текущего аутентифицированного пользователя.
     * Используется для проверки, пытается ли пользователь изменить собственный профиль.
     *
     * @param userId идентификатор пользователя для проверки
     * @return true, если указанный идентификатор совпадает с идентификатором текущего пользователя, иначе false
     */
    public final boolean isSelf(Long userId) {
        var currentUser = userUtils.getCurrentUser();
        return currentUser.getId().equals(userId);
    }
}
