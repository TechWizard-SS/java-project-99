package hexlet.code.controller;

import hexlet.code.BaseTest;
import hexlet.code.config.JwtRequestFilter;
import hexlet.code.config.JwtUtil;
import hexlet.code.config.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtRequestFilterTest extends BaseTest {

    private JwtRequestFilter filter;
    private MyUserDetailsService userDetailsService;
    private JwtUtil jwtUtil;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        userDetailsService = mock(MyUserDetailsService.class);
        jwtUtil = mock(JwtUtil.class);
        filterChain = mock(FilterChain.class);
        filter = new JwtRequestFilter(userDetailsService, jwtUtil);
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/api/login", "/", "/index.html", "/favicon.ico", "/assets/js/app.js"})
    void testShouldNotFilter(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath(path);

        // Вызываем напрямую, так как мы в одном пакете
        boolean result = filter.shouldNotFilter(request);
        assertThat(result).isTrue();
    }

    @Test
    void testShouldFilterOtherPaths() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/users");
        assertThat(filter.shouldNotFilter(request)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void testTokenEmptyOrNullString(String tokenValue) throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokenValue);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        // Проверяем, что аутентификация не установилась, но цепочка продолжилась
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void testSecurityContextAlreadyHasAuthentication() throws ServletException, IOException {
        // Имитируем, что пользователь уже залогинен (например, другим фильтром)
        var existingAuth = mock(org.springframework.security.core.Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.extractUsername("valid_token")).thenReturn("user@mail.com");

        filter.doFilterInternal(request, response, filterChain);

        // Проверяем, что существующая аутентификация не перезаписалась
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(existingAuth);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void testExpiredJwtException() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer expired_token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.extractUsername("expired_token")).thenThrow(ExpiredJwtException.class);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testInvalidJwtFormat() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid_garbage");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.extractUsername("invalid_garbage")).thenThrow(new RuntimeException("Bad format"));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testTokenValidationFailed() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer wrong_user_token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        String username = "testuser";
        UserDetails userDetails = new User(username, "pass", List.of());

        when(jwtUtil.extractUsername("wrong_user_token")).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        // Валидация не прошла
        when(jwtUtil.validateToken("wrong_user_token", userDetails)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}