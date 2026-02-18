package hexlet.code.repository;

import hexlet.code.model.Task;
import hexlet.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для сущности {@link Task}.
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository},
 * а также методы для специфичных запросов к задачам и возможность
 * выполнения сложных запросов с использованием спецификаций ({@link JpaSpecificationExecutor}).
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    /**
     * Находит задачу по её названию.
     *
     * @param name название задачи для поиска
     * @return {@link Optional}, содержащий найденную задачу, или {@link Optional#empty()}, если задача не найдена
     */
    Optional<Task> findByName(String name);

    /**
     * Находит список задач, назначенных указанному пользователю.
     *
     * @param assignee пользователь-исполнитель
     * @return список задач, назначенных пользователю {@code assignee}
     */
    List<Task> findByAssignee(User assignee);

    /**
     * Находит список задач, назначенных пользователю с указанным идентификатором.
     *
     * @param assigneeId идентификатор пользователя-исполнителя
     * @return список задач, назначенных пользователю с ID {@code assigneeId}
     */
    List<Task> findByAssigneeId(Long assigneeId);

    /**
     * Проверяет, существуют ли задачи, назначенные пользователю с указанным идентификатором.
     *
     * @param assigneeId идентификатор пользователя-исполнителя
     * @return true, если существуют задачи, назначенные пользователю с ID {@code assigneeId}, иначе false
     */
    boolean existsByAssigneeId(Long assigneeId);

    /**
     * Проверяет, существуют ли задачи с указанным идентификатором статуса.
     *
     * @param statusId идентификатор статуса задачи
     * @return true, если существуют задачи со статусом с ID {@code statusId}, иначе false
     */
    boolean existsByTaskStatusId(Long statusId);

    /**
     * Проверяет, существуют ли задачи, помеченные меткой с указанным идентификатором.
     *
     * @param labelId идентификатор метки
     * @return true, если существуют задачи, помеченные меткой с ID {@code labelId}, иначе false
     */
    boolean existsByLabelsId(Long labelId);
}
