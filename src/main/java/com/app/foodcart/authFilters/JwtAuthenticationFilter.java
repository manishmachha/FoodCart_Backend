package com.app.foodcart.authFilters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.foodcart.authUtils.JwtUtil;
import com.app.foodcart.services.customUserDetails.CustomUserDetailsService;
import com.app.foodcart.services.tokenBlacklist.BlackListService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@SuppressWarnings("all")
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private BlackListService blacklistService;

    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/api/users/create",
            "/api/auth/**"
    // "/api/foods/**",
    // "/api/restaurants/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        logger.info("Processing request for path: {}", path);

        // Check if the request path is excluded
        if (isExcludedPath(path)) {
            logger.info("Path is excluded from authentication: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            logger.info("Authorization header found: {}", authorizationHeader);
        } else {
            logger.warn("Authorization header is missing");
        }

        String token = null;
        String username = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                sendErrorResponse(response, "Invalid token", HttpStatus.UNAUTHORIZED);
                return;
            }
        } else if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, "Missing or malformed token", HttpStatus.BAD_REQUEST);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Username extracted from token: {}", username);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, userDetails)) {
                logger.info("Token is valid for user: {}", username);
                if (blacklistService.isTokenBlacklisted(token)) {
                    logger.warn("Token is blacklisted: {}", token);
                    sendErrorResponse(response, "Token is blacklisted", HttpStatus.UNAUTHORIZED);
                    return;
                }
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.warn("Token validation failed for user: {}", username);
                sendErrorResponse(response, "Token validation failed", HttpStatus.UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", message);
        errorDetails.put("status", status.toString());
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorDetails));
    }

    private boolean isExcludedPath(String servletPath) {
        // Match exact paths or patterns if required
        return EXCLUDED_PATHS.stream()
                .anyMatch(path -> servletPath.equals(path) || servletPath.startsWith(path.replace("/**", "")));
    }
}