package hexlet.code.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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
 * Configuration class for security settings.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    private final MyUserDetailsService myUserDetailsService;

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtRequestFilter jwtRequestFilter,
                          MyUserDetailsService myUserDetailsService) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
        this.myUserDetailsService = myUserDetailsService;
    }

    /**
     * Creates a bean for password encoding using BCrypt algorithm.
     *
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates a bean for authentication manager.
     *
     * @param authConfig authentication configuration
     * @return authentication manager
     * @throws Exception if authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Creates a bean for security filter chain with configured security rules.
     *
     * @param http http security builder
     * @return configured security filter chain
     * @throws Exception if filter chain cannot be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                //.cors(Customizer.withDefaults()) // Включаем наш CORS конфиг (см. ниже)
                .httpBasic(AbstractHttpConfigurer::disable) // Отключаем Basic Auth, чтобы не лезло окно браузера
                // САМОЕ ВАЖНОЕ: Отключаем сессии. Только JWT!
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Статика и логин
                        .anyRequest().permitAll()

//                        .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico").permitAll()
//                        .requestMatchers("/api/login").permitAll()
//
//                        // Пользователи:
//                        // Создание и просмотр списка - всем (как у тебя было)
//                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()

                        // Всё остальное (UPDATE, DELETE, GET ONE) - только с токеном
//                                                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    // Этот бин нужен, чтобы фронтенд видел заголовок X-Total-Count и мог слать токены
    /**
     * Creates a bean for authentication provider with user details service and password encoder.
     *
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // Или "http://localhost:*"
        config.addAllowedHeader("Authorization");
        config.addAllowedMethod("Content-Type");
        config.addAllowedMethod("*");
        config.addExposedHeader("X-Total-Count"); // ВАЖНО для React Admin
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * Creates a bean for authentication provider with user details service and password encoder.
     *
     * @return authentication provider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(myUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
