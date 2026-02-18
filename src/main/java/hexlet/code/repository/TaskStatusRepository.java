package hexlet.code.repository;

import hexlet.code.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для сущности {@link TaskStatus}.
 * Предоставляет стандартные CRUD-операции, а также специфичные методы для работы со статусами задач.
 */
@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    /**
     * Находит статус задачи по его слагу.
     *
     * @param slug слаг статуса задачи для поиска
     * @return {@link Optional}, содержащий найденный статус задачи, или {@link Optional#empty()}, если статус не найден
     */
    Optional<TaskStatus> findBySlug(String slug);
}
