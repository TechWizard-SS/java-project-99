package hexlet.code.dto.Label;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode
public class LabelDTO {
    private Long id;
    private String name;
    private Instant createdAt;
}
