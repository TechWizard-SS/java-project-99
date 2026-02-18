package hexlet.code.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки параметров сериализации/десериализации JSON.
 * В частности, регистрирует модуль для корректной обработки типов JsonNullable.
 */
@Configuration
public class JacksonConfig {

    /**
     * Создаёт и регистрирует бин {@link JsonNullableModule}.
     * Этот модуль необходим для правильной сериализации и десериализации
     * свойств сущностей, использующих тип {@link org.openapitools.jackson.nullable.JsonNullable}.
     *
     * @return экземпляр {@link JsonNullableModule}
     */
    @Bean
    public JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }
}
