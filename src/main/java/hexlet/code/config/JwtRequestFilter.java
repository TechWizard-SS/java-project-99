package hexlet.code.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Фильтр Spring Security, который перехватывает каждый HTTP-запрос
 * для извлечения и проверки JWT-токена из заголовка Authorization.
 * Если токен действителен, аутентифицированный пользователь устанавливается
 * в SecurityContext для дальнейшей авторизации.
 * Запросы к маршрутам, указанным в {@link #shouldNotFilter}, не проходят через этот фильтр.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public final class JwtRequestFilter extends OncePerRequestFilter {

    private final MyUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Определяет, должен ли данный запрос быть исключен из фильтрации этим фильтром.
     * Запросы к маршрутам аутентификации и статическим ресурсам не проходят проверку JWT.
     *
     * @param request объект {@link HttpServletRequest}, представляющий входящий HTTP-запрос
     * @return true, если фильтр должен быть пропущен для этого запроса, иначе false
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String path = request.getServletPath();

        return pathMatcher.match("/api/login", path)
                || pathMatcher.match("/", path)
                || pathMatcher.match("/index.html", path)
                || pathMatcher.match("/favicon.ico", path)
                || pathMatcher.match("/assets/**", path);
    }


    /**
     * Основной метод фильтрации. Извлекает токен из заголовка Authorization,
     * проверяет его действительность и, при успехе, устанавливает аутентифицированного
     * пользователя в SecurityContext.
     *
     * @param request  объект {@link HttpServletRequest}, представляющий входящий HTTP-запрос
     * @param response объект {@link HttpServletResponse}, представляющий исходящий HTTP-ответ
     * @param chain    объект {@link FilterChain}, используемый для вызова следующего фильтра в цепочке
     * @throws ServletException если возникает ошибка, связанная с сервлетом
     * @throws IOException      если возникает ошибка ввода-вывода при обработке запроса/ответа
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        log.debug("Processing request for URI: {}, Authorization Header: {}", request.getRequestURI(), header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            log.debug("Extracted token: '{}'", token);

            if (token == null || token.isEmpty() || "null".equals(token)) {
                log.debug("Token is empty or string 'null', skipping authentication.");
                chain.doFilter(request, response);
                return;
            }

            try {
                String username = jwtUtil.extractUsername(token);
                log.debug("Successfully extracted username '{}' from token.", username);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.debug("Authenticated user '{}' and set security context.", username);
                    } else {
                        log.warn("JWT Token is valid format but validation failed (e.g., expired or wrong user).");
                    }
                } else {
                    log.debug("Security Context already had an Authentication object, skipping.");
                }
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                log.warn("Expired JWT: {}", e.getMessage());
            } catch (Exception e) {
                log.warn("Invalid JWT format: {}. Token was: '{}'", e.getMessage(), token);
            }
        } else {
            log.debug("No valid 'Authorization: Bearer ...' header found. Skipping JWT filter logic.");
        }

        chain.doFilter(request, response);
    }
}
