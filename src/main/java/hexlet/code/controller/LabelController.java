package hexlet.code.controller;

import hexlet.code.model.dto.Label.LabelCreateDTO;
import hexlet.code.model.dto.Label.LabelDTO;
import hexlet.code.model.dto.Label.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import hexlet.code.util.NamedRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

/**
 * Контроллер для управления метками ({@link hexlet.code.model.Label}).
 * Обрабатывает HTTP-запросы для получения списка меток, просмотра,
 * создания, обновления и удаления отдельных меток.
 */
@RestController
@RequestMapping(NamedRoutes.LABELS)
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    /**
     * Обрабатывает GET-запрос на получение списка всех меток.
     * Возвращает список DTO меток и заголовок X-Total-Count.
     *
     * @return {@link ResponseEntity} с HTTP статусом 200 OK и телом, содержащим список {@link LabelDTO}
     */
    @GetMapping("")
    public ResponseEntity<List<LabelDTO>> index() {
        var labels = labelService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    /**
     * Обрабатывает GET-запрос на получение метки по её идентификатору.
     *
     * @param id идентификатор метки
     * @return DTO найденной метки {@link LabelDTO}
     */
    @GetMapping(NamedRoutes.LABEL_ID)
    public LabelDTO show(@PathVariable Long id) {
        return labelService.findById(id);
    }

    /**
     * Обрабатывает POST-запрос на создание новой метки.
     * Принимает DTO с данными для создания.
     *
     * @param labelData DTO {@link LabelCreateDTO} с данными новой метки
     * @return DTO созданной метки {@link LabelDTO}
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDTO create(@Valid @RequestBody LabelCreateDTO labelData) {
        return labelService.create(labelData);
    }

    /**
     * Обрабатывает PUT-запрос на обновление существующей метки.
     * Принимает DTO с новыми данными и идентификатор метки.
     *
     * @param labelData DTO {@link LabelUpdateDTO} с новыми данными метки
     * @param id        идентификатор обновляемой метки
     * @return DTO обновлённой метки {@link LabelDTO}
     */
    @PutMapping(NamedRoutes.LABEL_ID)
    public LabelDTO update(@Valid @RequestBody LabelUpdateDTO labelData, @PathVariable Long id) {
        return labelService.update(labelData, id);
    }

    /**
     * Обрабатывает DELETE-запрос на удаление метки по её идентификатору.
     *
     * @param id идентификатор удаляемой метки
     */
    @DeleteMapping(NamedRoutes.LABEL_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id) {
        labelService.delete(id);
    }
}
