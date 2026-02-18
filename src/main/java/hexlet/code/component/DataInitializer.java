package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Компонент для начальной инициализации данных при запуске приложения.
 * Создаёт предопределённые статусы задач и метки, если они ещё не существуют в базе данных.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TaskStatusRepository statusRepository;
    private final LabelRepository labelRepository;

    /**
     * Выполняет начальную инициализацию данных.
     * Создаёт стандартные статусы задач (draft, to_review, to_be_fixed, to_publish, published)
     * и метки (feature, bug), если они отсутствуют в базе данных.
     *
     * @param args аргументы командной строки (не используются в данной реализации)
     */
    @Override
    public void run(String... args) {
        // Инициализация статусов
        List.of("draft", "to_review", "to_be_fixed", "to_publish", "published")
                .forEach(slug -> {
                    if (statusRepository.findBySlug(slug).isEmpty()) {
                        var status = new TaskStatus();
                        status.setName(slug);
                        status.setSlug(slug);
                        statusRepository.save(status);
                    }
                });

        // Инициализация меток
        List.of("feature", "bug")
                .forEach(name -> {
                    if (labelRepository.findByName(name).isEmpty()) {
                        var label = new Label();
                        label.setName(name);
                        labelRepository.save(label);
                    }
                });
    }
}
