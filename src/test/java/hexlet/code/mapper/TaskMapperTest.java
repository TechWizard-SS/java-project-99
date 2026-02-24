package hexlet.code.mapper;

import hexlet.code.BaseTest;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.model.dto.Label.LabelCreateDTO;
import hexlet.code.model.dto.Label.LabelUpdateDTO;
import hexlet.code.model.dto.Task.TaskCreateDTO;
import hexlet.code.model.dto.Task.TaskUpdateDTO;
import hexlet.code.model.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.model.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.model.dto.User.UserCreateDTO;
import hexlet.code.model.dto.User.UserUpdateDTO;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskMapperTest extends BaseTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ReferenceMapper referenceMapper;

    @Autowired
    private JsonNullableMapper jsonNullableMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;


    private User testUser;
    private TaskStatus testStatus;
    private Label testLabel;

    @BeforeEach
    public void setUp() {
        testUser = userRepository.findByEmail("hexlet1@example.com").orElseThrow();

        testStatus = new TaskStatus();
        testStatus.setName("New");
        testStatus.setSlug("new");
        taskStatusRepository.save(testStatus);

        testLabel = new Label();
        testLabel.setName("Bug");
        labelRepository.save(testLabel);
    }

    @Test
    public void testJsonNullableMapper() {
        JsonNullable<String> present = jsonNullableMapper.wrap("value");
        assertThat(jsonNullableMapper.isPresent(present)).isTrue();
        assertThat(jsonNullableMapper.unwrap(present)).isEqualTo("value");

        JsonNullable<String> undefined = JsonNullable.undefined();
        assertThat(jsonNullableMapper.isPresent(undefined)).isFalse();

        assertThat(Optional.ofNullable(jsonNullableMapper.unwrap(null))).isEqualTo(Optional.empty());
        assertThat(jsonNullableMapper.isPresent(null)).isFalse();
    }

    @Test
    public void testReferenceMapper() {
        User mappedUser = referenceMapper.toEntity(testUser.getId(), User.class);
        assertThat(mappedUser.getId()).isEqualTo(testUser.getId());

        // Покрываем ветку, когда ID равен null
        assertThat(referenceMapper.toEntity(null, User.class)).isNull();
    }

    @Test
    public void testUserMapper() {
        var createDto = new UserCreateDTO();
        createDto.setFirstName("John");
        createDto.setLastName("Doe");
        createDto.setEmail("john@test.com");
        createDto.setPassword("pass");

        User user = userMapper.map(createDto);
        assertThat(user.getFirstName()).isEqualTo("John");

        var mappedDto = userMapper.map(user);
        assertThat(mappedDto.getEmail()).isEqualTo("john@test.com");

        var updateDto = new UserUpdateDTO();
        updateDto.setFirstName(JsonNullable.of("Jane"));
        userMapper.update(updateDto, user);
        assertThat(user.getFirstName()).isEqualTo("Jane");
    }

    @Test
    public void testTaskStatusMapper() {
        var createDto = new TaskStatusCreateDTO();
        createDto.setName("In Progress");
        createDto.setSlug("in_progress");

        TaskStatus status = taskStatusMapper.map(createDto);
        var mappedDto = taskStatusMapper.map(status);
        assertThat(mappedDto.getSlug()).isEqualTo("in_progress");

        var updateDto = new TaskStatusUpdateDTO();
        updateDto.setName(JsonNullable.of("Done"));
        taskStatusMapper.update(updateDto, status);
        assertThat(status.getName()).isEqualTo("Done");
    }

    @Test
    public void testLabelMapper() {
        var createDto = new LabelCreateDTO();
        createDto.setName("Feature");

        Label label = labelMapper.map(createDto);
        var mappedDto = labelMapper.map(label);
        assertThat(mappedDto.getName()).isEqualTo("Feature");

        var updateDto = new LabelUpdateDTO();
        updateDto.setName(JsonNullable.of("Hotfix"));
        labelMapper.update(updateDto, label);
        assertThat(label.getName()).isEqualTo("Hotfix");
    }

    @Test
    public void testTaskMapper() {
        // 1. Create DTO -> Entity
        var createDto = new TaskCreateDTO();
        createDto.setTitle("Task 1");
        createDto.setContent("Description");
        createDto.setStatus(testStatus.getSlug());
        createDto.setAssigneeId(testUser.getId());
        createDto.setLabelIds(Set.of(testLabel.getId()));

        Task task = taskMapper.map(createDto);
        task = taskRepository.save(task);

        assertThat(task.getName()).isEqualTo("Task 1");
        assertThat(task.getTaskStatus().getSlug()).isEqualTo("new");
        assertThat(task.getAssignee().getId()).isEqualTo(testUser.getId());

        // 2. Entity -> DTO
        var mappedDto = taskMapper.map(task);
        assertThat(mappedDto.getTitle()).isEqualTo("Task 1");
        assertThat(mappedDto.getLabelIds()).contains(testLabel.getId());

        // 3. Update DTO -> Entity (JsonNullable)
        var updateDto = new TaskUpdateDTO();
        updateDto.setTitle(JsonNullable.of("Updated Task"));
        updateDto.setAssigneeId(JsonNullable.of(null)); // Проверка снятия исполнителя

        taskMapper.update(updateDto, task);
        assertThat(task.getName()).isEqualTo("Updated Task");

        // 4. Покрываем хелперы (красные ветки с null)
        assertThat(taskMapper.idToUser(JsonNullable.undefined())).isNull();
        assertThat(taskMapper.slugToStatus(JsonNullable.undefined())).isNull();
        assertThat(taskMapper.idsToLabels(JsonNullable.undefined())).isNull();

        assertThat(taskMapper.idToUserRaw(null)).isNull();
        assertThat(taskMapper.slugToStatusRaw(null)).isNull();
        assertThat(taskMapper.idsToLabelsRaw(null)).isNull();
        assertThat(taskMapper.labelsToIds(null)).isNull();
    }

    @Test
    public void testTaskMapperJsonNullableEdgeCases() {
        Task task = new Task();
        task.setName("Original");

        var updateDto = new TaskUpdateDTO();
        // Случай: поле в JSON пришло как null (JsonNullable.of(null))
        updateDto.setTitle(JsonNullable.of(null));
        updateDto.setAssigneeId(JsonNullable.of(null));

        taskMapper.update(updateDto, task);

        assertThat(task.getName()).isNull();
        assertThat(task.getAssignee()).isNull();
    }
}
