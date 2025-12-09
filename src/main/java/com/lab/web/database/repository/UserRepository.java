package com.lab.web.database.repository;

import com.lab.web.data.User;

public interface UserRepository {
    boolean isUserExist(User user);

    void createUser(User user);

    boolean checkPassword(User user);

    String generateToken(User user);

    String getToken(User user);

    User getUserByToken(String token);

    void invalidateToken(String token);
}
