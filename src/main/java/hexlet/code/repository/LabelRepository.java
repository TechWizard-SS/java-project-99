package hexlet.code.repository;

import hexlet.code.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Репозиторий для сущности {@link Label}.
 * Предоставляет стандартные CRUD-операции, а также специфичные методы для работы с метками.
 */
public interface LabelRepository extends JpaRepository<Label, Long> {

    /**
     * Находит метку по её имени.
     *
     * @param name имя метки для поиска
     * @return {@link Optional}, содержащий найденную метку, или {@link Optional#empty()}, если метка не найдена
     */
    Optional<Label> findByName(String name);
}
