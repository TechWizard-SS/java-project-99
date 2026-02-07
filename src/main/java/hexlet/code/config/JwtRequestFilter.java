package hexlet.code.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public final class JwtRequestFilter extends OncePerRequestFilter {

    private final MyUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            String raw = authorizationHeader.substring(7).trim();

            if ("[object Object]".equals(raw)) {
                log.warn("JWT is JS object, not string");
                chain.doFilter(request, response);
                return;
            }

            // JSON {"token": "..."}
            if (raw.startsWith("{")) {
                try {
                    JsonNode node = new ObjectMapper().readTree(raw);
                    if (node.has("token")) {
                        raw = node.get("token").asText();
                    }
                } catch (Exception e) {
                    log.warn("Cannot parse JWT JSON");
                    chain.doFilter(request, response);
                    return;
                }
            }

            // JWT обязан иметь 3 части
            if (!raw.matches("^[^.]+\\.[^.]+\\.[^.]+$")) {
                log.warn("Invalid JWT format: {}", raw);
                chain.doFilter(request, response);
                return;
            }

            try {
                String username = jwtUtil.extractUsername(raw);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails =
                            userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(raw, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                log.warn("JWT processing error: {}", e.getMessage());
            }
        }

        chain.doFilter(request, response);

    }
}