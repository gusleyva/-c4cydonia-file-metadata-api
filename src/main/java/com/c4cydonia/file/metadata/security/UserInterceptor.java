package com.c4cydonia.file.metadata.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // Allow public access to the root path and Swagger UI paths
        if (requestURI.equals("/")
                || requestURI.startsWith("/swagger-ui.html")
                || requestURI.startsWith("/v2/api-docs")
                || requestURI.startsWith("/swagger-resources")
                || requestURI.startsWith("/webjars")) {
            return true;
        }

        String userToken = request.getHeader("Authorization");
        if (userToken == null || !userToken.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // Assuming the token structure is "Bearer email@example.com"
        String token = userToken.substring(7); // Remove "Bearer " prefix
        String email = extractEmailFromToken(token); // Extract email from token

        if (email == null || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        request.setAttribute("email", email); // Set email as request attribute
        return true;
    }

    private String extractEmailFromToken(String token) {
        // Implement your logic to extract the email from the token
        // This is a simplified example; in real scenarios, you might need to decode a JWT or similar
        return token; // Here we assume the token itself is the email
    }

    private String extractEmailFromJwtToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("email").asString(); // Assuming the email is stored in the "email" claim
        } catch (Exception e) {
            return null;
        }
    }
}
