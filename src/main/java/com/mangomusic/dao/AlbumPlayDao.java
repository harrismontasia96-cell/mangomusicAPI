package com.mangomusic.dao;

import com.mangomusic.model.AlbumPlay;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AlbumPlayDao {

    private final DataSource dataSource;

    public AlbumPlayDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<AlbumPlay> getUserRecentPlays(int userId, int limit) {
        List<AlbumPlay> plays = new ArrayList<>();
        String query = "SELECT ap.play_id, ap.user_id, ap.album_id, ap.played_at, ap.completed, " +
                "       al.title as album_title, ar.name as artist_name " +
                "FROM album_plays ap " +
                "JOIN albums al ON ap.album_id = al.album_id " +
                "JOIN artists ar ON al.artist_id = ar.artist_id " +
                "WHERE ap.user_id = ? " +
                "ORDER BY ap.played_at DESC " +
                "LIMIT ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setInt(2, limit);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    AlbumPlay play = mapRowToAlbumPlay(results);
                    plays.add(play);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting user recent plays", e);
        }

        return plays;
    }

    public List<AlbumPlay> getAlbumPlays(int albumId) {
        List<AlbumPlay> plays = new ArrayList<>();
        String query = "SELECT ap.play_id, ap.user_id, ap.album_id, ap.played_at, ap.completed, " +
                "       al.title as album_title, ar.name as artist_name " +
                "FROM album_plays ap " +
                "JOIN albums al ON ap.album_id = al.album_id " +
                "JOIN artists ar ON al.artist_id = ar.artist_id " +
                "WHERE ap.album_id = ? " +
                "ORDER BY ap.played_at DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, albumId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    AlbumPlay play = mapRowToAlbumPlay(results);
                    plays.add(play);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting album plays", e);
        }

        return plays;
    }

    public AlbumPlay getPlayById(long playId) {
        String query = "SELECT ap.play_id, ap.user_id, ap.album_id, ap.played_at, ap.completed, " +
                "       al.title as album_title, ar.name as artist_name " +
                "FROM album_plays ap " +
                "JOIN albums al ON ap.album_id = al.album_id " +
                "JOIN artists ar ON al.artist_id = ar.artist_id " +
                "WHERE ap.play_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, playId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapRowToAlbumPlay(results);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting play by ID", e);
        }

        return null;
    }

    public AlbumPlay createPlay(AlbumPlay play) {
        String query = "INSERT INTO album_plays (user_id, album_id, played_at, completed) VALUES (?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, play.getUserId());
            statement.setInt(2, play.getAlbumId());
            statement.setObject(3, play.getPlayedAt());
            statement.setBoolean(4, play.isCompleted());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long playId = keys.getLong(1);
                    return getPlayById(playId);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating play", e);
        }

        return null;
    }

    public boolean deletePlay(long playId) {
        String query = "DELETE FROM album_plays WHERE play_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, playId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting play", e);
        }
    }

    private AlbumPlay mapRowToAlbumPlay(ResultSet results) throws SQLException {
        AlbumPlay play = new AlbumPlay();
        play.setPlayId(results.getLong("play_id"));
        play.setUserId(results.getInt("user_id"));
        play.setAlbumId(results.getInt("album_id"));

        Timestamp playedAt = results.getTimestamp("played_at");
        if (playedAt != null) {
            play.setPlayedAt(playedAt.toLocalDateTime());
        }

        play.setCompleted(results.getBoolean("completed"));
        play.setAlbumTitle(results.getString("album_title"));
        play.setArtistName(results.getString("artist_name"));

        return play;
    }
}