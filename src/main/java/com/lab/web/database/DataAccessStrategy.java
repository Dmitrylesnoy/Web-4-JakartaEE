package com.lab.web.database;

import java.util.List;

import com.lab.web.data.PointData;
import com.lab.web.data.User;

public interface DataAccessStrategy {
    List<PointData> getAllPoints(Long userId);

    void addPoint(PointData point);

    boolean isUserExist(User user);

    void createUser(User user);

    boolean checkPassword(User user);

    void generateToken(User user);

    String getToken(User user);

    User getUserByToken(String token);

    void invalidateToken(String token);
}
