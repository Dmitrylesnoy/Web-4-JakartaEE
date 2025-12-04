package com.lab.web.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.lab.web.data.PointData;
import com.lab.web.data.User;

import jakarta.transaction.Transactional;

@Transactional
public class JDBCDataAccess implements DataAccessStrategy {
    private DataSource dataSource;

    public JDBCDataAccess() {
        try {
            InitialContext ctx = new InitialContext();
            this.dataSource = (DataSource) ctx.lookup("java:/jboss/datasources/MyPostgresDS");
        } catch (NamingException e) {
            throw new RuntimeException("Failed to lookup DataSource", e);
        }
    }

    @Override
    public List<PointData> getAllPoints() {
        List<PointData> points = new ArrayList<>();
        String sql = "SELECT id, x, y, r, hit, exec_time, date FROM point_data ORDER BY date DESC";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                PointData point = new PointData();
                point.setId(rs.getLong("id"));
                point.setX(rs.getFloat("x"));
                point.setY(rs.getFloat("y"));
                point.setR(rs.getFloat("r"));
                point.setHit(rs.getBoolean("hit"));
                point.setExecTime(rs.getLong("exec_time"));
                point.setDate(rs.getTimestamp("date").toLocalDateTime());
                points.add(point);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve points", e);
        }
        return points;
    }

    @Override
    public void addPoint(PointData point) {
        System.out.println("JDBC: point added");
        String sql = "INSERT INTO point_data (x, y, r, hit, exec_time, date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, point.getX());
            stmt.setFloat(2, point.getY());
            stmt.setFloat(3, point.getR());
            stmt.setBoolean(4, point.isHit());
            stmt.setLong(5, point.getExecTime());
            stmt.setTimestamp(6, Timestamp.valueOf(point.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add point", e);
        }
    }

    @Override
    public boolean isUserExist(User user) {
        String sql = "SELECT COUNT(*) FROM web_users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if user exists", e);
        }
    }

    @Override
    public void createUser(User user) {
        System.out.println("JDBC: user created - " + user.username());
        String sql = "INSERT INTO web_users (username, password, token) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, user.password());
            stmt.setString(3, user.token());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public boolean checkPassword(User user) {
        String sql = "SELECT COUNT(*) FROM web_users WHERE username = ? AND password = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, user.password());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check password", e);
        }
    }

    @Override
    public void generateToken(User user) {
        String sql = "UPDATE web_users SET token = ? WHERE username = ?";
        String newToken = UUID.randomUUID().toString();
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newToken);
            stmt.setString(2, user.username());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("User not found: " + user.username());
            }
            System.out.println("JDBC: token generated for user - " + user.username());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    @Override
    public String getToken(User user) {
        String sql = "SELECT token FROM web_users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("token");
            } else {
                throw new RuntimeException("User not found: " + user.username());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get token", e);
        }
    }

    @Override
    public User getUserByToken(String token) {
        String sql = "SELECT id, username, password, token FROM web_users WHERE token = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("token"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user by token", e);
        }
    }

    @Override
    public void invalidateToken(String token) {
        String sql = "UPDATE web_users SET token = NULL WHERE token = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("JDBC: token invalidated - " + token);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to invalidate token", e);
        }
    }
}
