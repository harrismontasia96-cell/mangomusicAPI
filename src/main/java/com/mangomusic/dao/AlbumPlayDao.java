package com.mangomusic.dao;

import com.mangomusic.model.AlbumPlay;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import java.util.Map;
import java.util.HashMap;
@Repository
public class AlbumPlayDao {
    public List<Map<String, Object>> getTrendingAlbums(int days) {
        // Ensure days is between 1 and 30
        if (days < 1) days = 1;
        if (days > 30) days = 30;

        String query = "SELECT al.album_id, al.artist_id, al.title, al.release_year, ar.name AS artist_name, " +
                "       COUNT(ap.play_id) AS recent_play_count " +
                "FROM albums al " +
                "JOIN artists ar ON al.artist_id = ar.artist_id " +
                "LEFT JOIN album_plays ap ON al.album_id = ap.album_id AND ap.played_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                "GROUP BY al.album_id, al.artist_id, al.title, al.release_year, ar.name " +
                "ORDER BY recent_play_count DESC " +
                "LIMIT 10";

        List<Map<String, Object>> trending = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, days);

            try (ResultSet rs = stmt.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    Map<String, Object> album = new HashMap<>();
                    album.put("albumId", rs.getInt("album_id"));
                    album.put("artistId", rs.getInt("artist_id"));
                    album.put("title", rs.getString("title"));
                    album.put("releaseYear", rs.getInt("release_year"));
                    album.put("artistName", rs.getString("artist_name"));
                    album.put("recentPlayCount", rs.getInt("recent_play_count"));
                    album.put("trendingRank", rank++);
                    trending.add(album);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching trending albums", e);
        }

        return trending;
    }

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
    public Map<String, Object> getAlbumPlayCount(int albumId) {
        String query =
                "SELECT al.album_id, al.title AS album_title, ar.name AS artist_name, " +
                        "       COUNT(ap.play_id) AS play_count " +
                        "FROM albums al " +
                        "JOIN artists ar ON al.artist_id = ar.artist_id " +
                        "LEFT JOIN album_plays ap ON al.album_id = ap.album_id " +
                        "WHERE al.album_id = ? " +
                        "GROUP BY al.album_id, al.title, ar.name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, albumId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("albumId", rs.getInt("album_id"));
                    result.put("albumTitle", rs.getString("album_title"));
                    result.put("artistName", rs.getString("artist_name"));
                    result.put("playCount", rs.getInt("play_count"));
                    return result;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting album play count", e);
        }

        return null;
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