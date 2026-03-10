package hexlet.code.service;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;

import java.util.List;

public interface LabelService {
    List<LabelDTO> getAll();
    LabelDTO findById(Long id);
    LabelDTO create(LabelCreateDTO labelData);
    LabelDTO update(LabelUpdateDTO labelData, Long id);
    void delete(Long id);
}
