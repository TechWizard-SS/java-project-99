package hexlet.code.component;

import hexlet.code.model.Task;
import hexlet.code.model.dto.TaskParamsDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Компонент для построения спецификаций JPA (Criteria API) для фильтрации задач ({@link Task}).
 * Позволяет динамически формировать условия WHERE SQL-запроса на основе переданных параметров фильтрации.
 */
@Component
public class TaskSpecification {

    /**
     * Создаёт спецификацию JPA для фильтрации задач на основе переданных параметров.
     *
     * @param params объект {@link TaskParamsDTO}, содержащий параметры фильтрации.
     *               Может содержать:
     *               - {@code titleCont}: фильтрация по частичному совпадению с именем задачи (без учёта регистра).
     *               - {@code assigneeId}: фильтрация по идентификатору назначенного пользователя.
     *               - {@code status}: фильтрация по слагу статуса задачи.
     *               - {@code labelId}: фильтрация по идентификатору метки (many-to-many связь).
     * @return объект {@link Specification<Task>}, который можно использовать в методах
     * {@link org.springframework.data.jpa.repository.JpaSpecificationExecutor} для фильтрации.
     */
    public Specification<Task> build(TaskParamsDTO params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params.getTitleCont() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + params.getTitleCont().toLowerCase() + "%"));
            }

            if (params.getAssigneeId() != null) {
                predicates.add(cb.equal(root.get("assignee").get("id"), params.getAssigneeId()));
            }

            if (params.getStatus() != null) {
                predicates.add(cb.equal(root.get("taskStatus").get("slug"), params.getStatus()));
            }

            if (params.getLabelId() != null) {
                // Для ManyToMany используем join
                predicates.add(cb.equal(root.join("labels").get("id"), params.getLabelId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
