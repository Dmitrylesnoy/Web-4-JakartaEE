package com.lab.web.utils.auth;

import java.util.logging.Logger;

import com.lab.web.data.User;
import com.lab.web.database.repository.UserRepository;
import com.lab.web.database.service.JDBCService;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;

public class UserVetification {
    private static final Logger logger = Logger.getLogger(UserVetification.class.getName());
    private static UserRepository dataAccess = JDBCService.getInstance();
    private static JwtService jwtService = new JwtService();

    private UserVetification() {
    }

    public static String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    private static String normalizeToken(String token) throws AuthException {
        if (token == null || token.trim().isEmpty()) {
            logger.warning("Fetch attempt without token");
            throw new AuthException("Token required");
        }
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    public static String extractUsername(String token) throws AuthException {
        token = normalizeToken(token);
        return jwtService.extractUsername(token);
    }

    public static void checkUserByToken(String token) throws AuthException {
        token = normalizeToken(token);
        try {
            jwtService.extractClaims(token);
        } catch (Exception e) {
            logger.warning("Fetch attempt with invalid token: " + e.getMessage());
            throw new AuthException("Invalid token");
        }
    }

    public static Long getUserIDbyToken(String token) throws AuthException {
        token = normalizeToken(token);
        try {
            Claims claims = jwtService.extractClaims(token);
            String username = claims.getSubject();

            User user = dataAccess.getUserByUsername(username);
            if (user == null) {
                throw new AuthException("User not found");
            }

            return user.id();
        } catch (Exception e) {
            logger.warning("Invalid token: " + e.getMessage());
            throw new AuthException("Invalid token");
        }
    }
}
