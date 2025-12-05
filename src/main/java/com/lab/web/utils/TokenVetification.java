package com.lab.web.utils;

import java.util.logging.Logger;

import com.lab.web.API.LoginResource;
import com.lab.web.data.User;
import com.lab.web.database.DataAccessStrategy;
import com.lab.web.database.JDBCDataAccess;

import jakarta.security.auth.message.AuthException;

public class TokenVetification {
    private static final Logger logger = Logger.getLogger(LoginResource.class.getName());
    private static DataAccessStrategy dataAccess = JDBCDataAccess.getInstance();

    public static void checkToken(String token) throws AuthException {
        if (token == null || token.trim().isEmpty()) {
            logger.warning("Fetch attempt without token");
            throw new AuthException("Token required");
        }
    }

    public static Long getUserIDbyToken(String token) {
        return dataAccess.getUserByToken(token).id();
    }

    public static void checkUserByToken(String token) throws AuthException {
        checkToken(token);
        User user = dataAccess.getUserByToken(token);
        if (user == null) {
            logger.warning("Fetch attempt with invalid token");
            throw new AuthException("Invalid token");
        }
    }
}
