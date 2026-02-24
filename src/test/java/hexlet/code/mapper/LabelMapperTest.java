package hexlet.code.mapper;

import hexlet.code.BaseTest;
import hexlet.code.model.Label;
import hexlet.code.model.dto.Label.LabelUpdateDTO;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LabelMapperTest extends BaseTest {

    @Autowired
    private LabelMapper labelMapper;
    @Test
    public void testLabelMapperDetailed() {
        Label label = new Label();
        label.setName("OldLabel");

        var updateDto = new LabelUpdateDTO();
        updateDto.setName(JsonNullable.of("Urgent"));

        labelMapper.update(updateDto, label);
        assertThat(label.getName()).isEqualTo("Urgent");
    }
}
