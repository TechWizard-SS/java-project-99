package hexlet.code.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Конфигурационный класс для настройки параметров безопасности Spring Security.
 * Включает настройки аутентификации, авторизации, фильтров и обработчиков исключений.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    private final MyUserDetailsService myUserDetailsService;

    /**
     * Создаёт бин для кодировщика паролей, используя алгоритм BCrypt.
     *
     * @return экземпляр {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создаёт бин для менеджера аутентификации.
     *
     * @param authConfig объект конфигурации аутентификации
     * @return экземпляр {@link AuthenticationManager}
     * @throws Exception если менеджер аутентификации не может быть создан
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Создаёт бин для цепочки фильтров безопасности с настроенными правилами.
     * Отключает CSRF и сессии, настраивает доступ к маршрутам и добавляет JWT-фильтр.
     *
     * @param http билдер {@link HttpSecurity} для настройки параметров HTTP-безопасности
     * @return настроенная цепочка фильтров безопасности {@link SecurityFilterChain}
     * @throws Exception если цепочка фильтров не может быть построена
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/login").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/task_statuses").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/task_statuses/**").permitAll()

                        .requestMatchers("/api/tasks/**").authenticated()

                                                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    // Этот бин нужен, чтобы фронтенд видел заголовок X-Total-Count и мог слать токены
    /**
     * Создаёт бин для фильтра CORS (Cross-Origin Resource Sharing).
     * Позволяет настроить политику CORS для приложения.
     *
     * @return экземпляр {@link CorsFilter}
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // Позволяет запросы с любого источника (осторожно!)
        config.addAllowedHeader("Authorization"); // Позволяет заголовок Authorization
        config.addAllowedMethod("Content-Type"); // Позволяет метод Content-Type
        config.addAllowedMethod("*"); // Позволяет все методы
        config.addExposedHeader("X-Total-Count"); // Позволяет клиенту видеть заголовок X-Total-Count
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * Создаёт бин для провайдера аутентификации, использующего UserDetailsService и PasswordEncoder.
     *
     * @return экземпляр {@link DaoAuthenticationProvider}
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(myUserDetailsService); // Устанавливаем сервис для загрузки пользователей
        authProvider.setPasswordEncoder(passwordEncoder()); // Устанавливаем кодировщик паролей
        return authProvider;
    }
}
