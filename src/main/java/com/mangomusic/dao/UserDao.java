package com.mangomusic.dao;

import com.mangomusic.model.User;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao {

    private final DataSource dataSource;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, email, signup_date, subscription_type, country " +
                "FROM users " +
                "ORDER BY username";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                User user = mapRowToUser(results);
                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting all users", e);
        }

        return users;
    }

    public User getUserById(int userId) {
        String query = "SELECT user_id, username, email, signup_date, subscription_type, country " +
                "FROM users " +
                "WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapRowToUser(results);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting user by ID", e);
        }

        return null;
    }

    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, email, signup_date, subscription_type, country " +
                "FROM users " +
                "WHERE username LIKE ? OR email LIKE ? " +
                "ORDER BY username";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    User user = mapRowToUser(results);
                    users.add(user);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error searching users", e);
        }

        return users;
    }

    public List<User> getUsersByCountry(String country) {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, email, signup_date, subscription_type, country " +
                "FROM users " +
                "WHERE country = ? " +
                "ORDER BY username";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, country);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    User user = mapRowToUser(results);
                    users.add(user);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting users by country", e);
        }

        return users;
    }

    public List<User> getUsersBySubscriptionType(String subscriptionType) {
        List<User> users = new ArrayList<>();
        String query = "SELECT user_id, username, email, signup_date, subscription_type, country " +
                "FROM users " +
                "WHERE subscription_type = ? " +
                "ORDER BY username";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, subscriptionType);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    User user = mapRowToUser(results);
                    users.add(user);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting users by subscription type", e);
        }

        return users;
    }

    public User createUser(User user) {
        String query = "INSERT INTO users (username, email, signup_date, subscription_type, country) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setObject(3, user.getSignupDate());
            statement.setString(4, user.getSubscriptionType());
            statement.setString(5, user.getCountry());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int userId = keys.getInt(1);
                    return getUserById(userId);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }

        return null;
    }

    public User updateUser(int userId, User user) {
        String query = "UPDATE users SET username = ?, email = ?, signup_date = ?, subscription_type = ?, country = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setObject(3, user.getSignupDate());
            statement.setString(4, user.getSubscriptionType());
            statement.setString(5, user.getCountry());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                return null;
            }

            return getUserById(userId);

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    private User mapRowToUser(ResultSet results) throws SQLException {
        User user = new User();
        user.setUserId(results.getInt("user_id"));
        user.setUsername(results.getString("username"));
        user.setEmail(results.getString("email"));

        Date signupDate = results.getDate("signup_date");
        if (signupDate != null) {
            user.setSignupDate(signupDate.toLocalDate());
        }

        user.setSubscriptionType(results.getString("subscription_type"));
        user.setCountry(results.getString("country"));
        return user;
    }
}