package hexlet.code.repository;

import hexlet.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Репозиторий для сущности {@link User}.
 * Предоставляет стандартные CRUD-операции, а также специфичные методы для работы с пользователями.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователя по его адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя для поиска
     * @return {@link Optional}, содержащий найденного пользователя, или {@link Optional#empty()},
     * если пользователь не найден
     */
    Optional<User> findByEmail(String email);
}
