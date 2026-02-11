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

        String header = request.getHeader("Authorization");
        log.debug("Processing request for URI: {}, Authorization Header: {}", request.getRequestURI(), header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            log.debug("Extracted token: '{}'", token);

            if (token.isEmpty()) {
                log.warn("Authorization header contains 'Bearer ' but no token follows.");
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
                log.warn("Invalid JWT (format error or other): {}. Token was: '{}'", e.getMessage(), token);
            }
        } else {
            log.debug("No valid 'Authorization: Bearer ...' header found. Skipping JWT filter logic.");
        }

        chain.doFilter(request, response);
    }
}