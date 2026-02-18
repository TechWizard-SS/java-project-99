package hexlet.code.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Утилитарный компонент для работы с JWT (JSON Web Token).
 * Предоставляет методы для генерации, извлечения данных и валидации токенов.
 */
@Component
public final class JwtUtil {

    /**
     * Секретный ключ для подписи JWT, загружаемый из конфигурационного файла (например, application.yml).
     */
    @Value("${spring.security.jwt.secret}")
    private String secretKey;

    /**
     * Время жизни токена в миллисекундах, загружаемое из конфигурационного файла.
     */
    @Value("${spring.security.jwt.expiration}")
    private long expiration;

    /**
     * Извлекает имя пользователя (subject) из JWT-токена.
     *
     * @param token JWT-токен, из которого извлекается имя пользователя
     * @return имя пользователя, указанное в токене
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Извлекает дату истечения срока действия из JWT-токена.
     *
     * @param token JWT-токен, из которого извлекается дата истечения
     * @return дата, когда токен становится недействительным
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает произвольное утверждение (claim) из JWT-токена с помощью переданной функции.
     *
     * @param token           JWT-токен, из которого извлекается утверждение
     * @param claimsResolver  функция, которая принимает объект {@link Claims} и возвращает требуемое значение
     * @param <T>             тип возвращаемого значения
     * @return результат применения {@code claimsResolver} к утверждениям токена
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Парсит JWT-токен и извлекает из него все утверждения (claims).
     * Использует секретный ключ для проверки подписи токена.
     *
     * @param token JWT-токен для парсинга
     * @return объект {@link Claims}, содержащий все утверждения токена
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Проверяет, истёк ли срок действия токена.
     *
     * @param token JWT-токен для проверки
     * @return true, если текущая дата позже даты истечения токена, иначе false
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Генерирует новый JWT-токен для указанного пользователя.
     *
     * @param userDetails объект {@link UserDetails}, содержащий информацию о пользователе
     * @return сгенерированный JWT-токен в виде строки
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Создаёт JWT-токен с указанными утверждениями и именем пользователя.
     *
     * @param claims   утверждения, которые будут включены в токен
     * @param subject  имя пользователя (subject), которое будет включено в токен
     * @return сгенерированный JWT-токен в виде строки
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Проверяет, является ли токен действительным для указанного пользователя.
     * Проверяет, совпадает ли имя пользователя в токене с именем пользователя в {@link UserDetails}
     * и не истёк ли срок действия токена.
     *
     * @param token        JWT-токен для проверки
     * @param userDetails  объект {@link UserDetails}, представляющий ожидаемого владельца токена
     * @return true, если токен действителен, иначе false
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
