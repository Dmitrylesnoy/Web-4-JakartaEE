package com.lab.web.database;

import java.util.List;
import java.util.UUID;

import com.lab.web.data.PointData;
import com.lab.web.data.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.persistence.Query;

@Transactional
public class HibernateDataAccess implements DataAccessStrategy {
    @PersistenceContext(unitName = "com.lab.web3")
    private EntityManager entityManager;

    @Override
    public List<PointData> getAllPoints() {
        TypedQuery<PointData> query = entityManager.createQuery("SELECT p FROM PointData p ORDER BY p.date DESC",
                PointData.class);
        return query.getResultList();
    }

    @Override
    public void addPoint(PointData point) {
        System.out.println("Hibernate: point added");
        entityManager.persist(point);
    }

    @Override
    public boolean isUserExist(User user) {
        Query query = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username");
        query.setParameter("username", user.username());
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public void createUser(User user) {
        entityManager.persist(user);
        System.out.println("Hibernate: user created - " + user.username());
    }

    @Override
    public boolean checkPassword(User user) {
        Query query = entityManager
                .createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password");
        query.setParameter("username", user.username());
        query.setParameter("password", user.password());
        try {
            User foundUser = (User) query.getSingleResult();
            return foundUser != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public void generateToken(User user) {
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username");
        query.setParameter("username", user.username());
        try {
            User existingUser = (User) query.getSingleResult();
            String newToken = UUID.randomUUID().toString();
            User updatedUser = new User(existingUser.id(), existingUser.username(), existingUser.password(), newToken);
            entityManager.merge(updatedUser);
            System.out.println("Hibernate: token generated for user - " + user.username());
        } catch (NoResultException e) {
            throw new RuntimeException("User not found: " + user.username(), e);
        }
    }

    @Override
    public String getToken(User user) {
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username");
        query.setParameter("username", user.username());
        try {
            User foundUser = (User) query.getSingleResult();
            return foundUser.token();
        } catch (NoResultException e) {
            throw new RuntimeException("User not found: " + user.username(), e);
        }
    }

    @Override
    public User getUserByToken(String token) {
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.token = :token");
        query.setParameter("token", token);
        try {
            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void invalidateToken(String token) {
        Query query = entityManager.createQuery("UPDATE User u SET u.token = NULL WHERE u.token = :token");
        query.setParameter("token", token);
        int rowsAffected = query.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Hibernate: token invalidated - " + token);
        }
    }
}
