package com.mangomusic.dao;

import com.mangomusic.model.ReportResult;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportsDao {

    private final DataSource dataSource;

    public ReportsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<ReportResult> getDailyActiveUsersReport() {
        List<ReportResult> results = new ArrayList<>();
        String query = "SELECT " +
                "    DATE(played_at) as activity_date, " +
                "    COUNT(DISTINCT user_id) as daily_active_users " +
                "FROM album_plays " +
                "WHERE played_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                "GROUP BY DATE(played_at) " +
                "ORDER BY activity_date DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                ReportResult result = new ReportResult();
                result.addColumn("activityDate", rs.getDate("activity_date").toLocalDate());
                result.addColumn("dailyActiveUsers", rs.getInt("daily_active_users"));
                results.add(result);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error running Daily Active Users report", e);
        }

        return results;
    }

    public List<ReportResult> getTopAlbumsThisMonthReport() {
        List<ReportResult> results = new ArrayList<>();
        String query = "SELECT " +
                "    al.title as album_title, " +
                "    ar.name as artist_name, " +
                "    COUNT(*) as play_count " +
                "FROM album_plays ap " +
                "JOIN albums al ON ap.album_id = al.album_id " +
                "JOIN artists ar ON al.artist_id = ar.artist_id " +
                "WHERE YEAR(ap.played_at) = YEAR(CURDATE()) " +
                "  AND MONTH(ap.played_at) = MONTH(CURDATE()) " +
                "GROUP BY al.album_id, al.title, ar.name " +
                "ORDER BY play_count DESC " +
                "LIMIT 10";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                ReportResult result = new ReportResult();
                result.addColumn("albumTitle", rs.getString("album_title"));
                result.addColumn("artistName", rs.getString("artist_name"));
                result.addColumn("playCount", rs.getInt("play_count"));
                results.add(result);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error running Top Albums This Month report", e);
        }

        return results;
    }

    public List<ReportResult> getGenrePopularityReport() {
        List<ReportResult> results = new ArrayList<>();
        String query = "SELECT " +
                "    ar.primary_genre as genre, " +
                "    COUNT(DISTINCT al.album_id) as total_albums, " +
                "    COUNT(ap.play_id) as total_plays, " +
                "    COUNT(DISTINCT ar.artist_id) as total_artists " +
                "FROM album_plays ap " +
                "JOIN albums al ON ap.album_id = al.album_id " +
                "JOIN artists ar ON al.artist_id = ar.artist_id " +
                "GROUP BY ar.primary_genre " +
                "ORDER BY total_plays DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                ReportResult result = new ReportResult();
                result.addColumn("genre", rs.getString("genre"));
                result.addColumn("totalAlbums", rs.getInt("total_albums"));
                result.addColumn("totalPlays", rs.getInt("total_plays"));
                result.addColumn("totalArtists", rs.getInt("total_artists"));
                results.add(result);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error running Genre Popularity report", e);
        }

        return results;
    }

    public List<ReportResult> getTopArtistsReport() {
        List<ReportResult> results = new ArrayList<>();
        String query = "SELECT " +
                "    ar.name as artist_name, " +
                "    ar.primary_genre as genre, " +
                "    COUNT(DISTINCT al.album_id) as total_albums_played, " +
                "    COUNT(ap.play_id) as total_plays, " +
                "    COUNT(DISTINCT ap.user_id) as unique_listeners " +
                "FROM album_plays ap " +
                "JOIN albums al ON ap.album_id = al.album_id " +
                "JOIN artists ar ON al.artist_id = ar.artist_id " +
                "GROUP BY ar.artist_id, ar.name, ar.primary_genre " +
                "ORDER BY total_plays DESC " +
                "LIMIT 20";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                ReportResult result = new ReportResult();
                result.addColumn("artistName", rs.getString("artist_name"));
                result.addColumn("genre", rs.getString("genre"));
                result.addColumn("totalAlbumsPlayed", rs.getInt("total_albums_played"));
                result.addColumn("totalPlays", rs.getInt("total_plays"));
                result.addColumn("uniqueListeners", rs.getInt("unique_listeners"));
                results.add(result);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error running Top Artists report", e);
        }

        return results;
    }

    public ReportResult getMangoMusicMapped(int userId) {
        ReportResult mapped = new ReportResult();

        try (Connection connection = dataSource.getConnection()) {

            // Get user's top artist of the year
            String topArtistQuery = "SELECT ar.name as artist_name, COUNT(*) as play_count " +
                    "FROM album_plays ap " +
                    "JOIN albums al ON ap.album_id = al.album_id " +
                    "JOIN artists ar ON al.artist_id = ar.artist_id " +
                    "WHERE ap.user_id = ? AND YEAR(ap.played_at) = YEAR(CURDATE()) " +
                    "GROUP BY ar.artist_id, ar.name " +
                    "ORDER BY play_count DESC " +
                    "LIMIT 1";

            try (PreparedStatement stmt = connection.prepareStatement(topArtistQuery)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        mapped.addColumn("topArtist", rs.getString("artist_name"));
                        mapped.addColumn("topArtistPlays", rs.getInt("play_count"));
                    } else {
                        mapped.addColumn("topArtist", "N/A");
                        mapped.addColumn("topArtistPlays", 0);
                    }
                }
            }

            // Get most active month this year
            String topMonthQuery = "SELECT DATE_FORMAT(played_at, '%M') as month_name, " +
                    "       COUNT(*) as play_count " +
                    "FROM album_plays " +
                    "WHERE user_id = ? AND YEAR(played_at) = YEAR(CURDATE()) " +
                    "GROUP BY MONTH(played_at), DATE_FORMAT(played_at, '%M') " +
                    "ORDER BY play_count DESC " +
                    "LIMIT 1";

            try (PreparedStatement stmt = connection.prepareStatement(topMonthQuery)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        mapped.addColumn("topMonth", rs.getString("month_name"));
                        mapped.addColumn("topMonthPlays", rs.getInt("play_count"));
                    } else {
                        mapped.addColumn("topMonth", "N/A");
                        mapped.addColumn("topMonthPlays", 0);
                    }
                }
            }

            // Get total plays this year
            String totalPlaysQuery = "SELECT COUNT(*) as total_plays, " +
                    "       COUNT(DISTINCT al.album_id) as unique_albums, " +
                    "       COUNT(DISTINCT ar.artist_id) as unique_artists, " +
                    "       SUM(CASE WHEN ap.completed = TRUE THEN 1 ELSE 0 END) as completed_plays " +
                    "FROM album_plays ap " +
                    "JOIN albums al ON ap.album_id = al.album_id " +
                    "JOIN artists ar ON al.artist_id = ar.artist_id " +
                    "WHERE ap.user_id = ? AND YEAR(ap.played_at) = YEAR(CURDATE())";

            try (PreparedStatement stmt = connection.prepareStatement(totalPlaysQuery)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        mapped.addColumn("totalPlays", rs.getInt("total_plays"));
                        mapped.addColumn("uniqueAlbums", rs.getInt("unique_albums"));
                        mapped.addColumn("uniqueArtists", rs.getInt("unique_artists"));
                        mapped.addColumn("completedPlays", rs.getInt("completed_plays"));
                    } else {
                        mapped.addColumn("totalPlays", 0);
                        mapped.addColumn("uniqueAlbums", 0);
                        mapped.addColumn("uniqueArtists", 0);
                        mapped.addColumn("completedPlays", 0);
                    }
                }
            }

            // Get top genre
            String topGenreQuery = "SELECT ar.primary_genre, COUNT(*) as play_count " +
                    "FROM album_plays ap " +
                    "JOIN albums al ON ap.album_id = al.album_id " +
                    "JOIN artists ar ON al.artist_id = ar.artist_id " +
                    "WHERE ap.user_id = ? AND YEAR(ap.played_at) = YEAR(CURDATE()) " +
                    "GROUP BY ar.primary_genre " +
                    "ORDER BY play_count DESC " +
                    "LIMIT 1";

            try (PreparedStatement stmt = connection.prepareStatement(topGenreQuery)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        mapped.addColumn("topGenre", rs.getString("primary_genre"));
                    } else {
                        mapped.addColumn("topGenre", "N/A");
                    }
                }
            }

            // Calculate listener personality
            int totalPlays = mapped.getInt("totalPlays");
            int uniqueArtists = mapped.getInt("uniqueArtists");
            int completedPlays = mapped.getInt("completedPlays");

            String personality = determineListenerPersonality(totalPlays, uniqueArtists, completedPlays);
            mapped.addColumn("listenerPersonality", personality);

            // Get longest streak
            String streakQuery = "WITH daily_plays AS ( " +
                    "    SELECT DISTINCT DATE(played_at) as play_date " +
                    "    FROM album_plays " +
                    "    WHERE user_id = ? AND YEAR(played_at) = YEAR(CURDATE()) " +
                    "), " +
                    "date_groups AS ( " +
                    "    SELECT " +
                    "        play_date, " +
                    "        DATE_SUB(play_date, INTERVAL ROW_NUMBER() OVER (ORDER BY play_date) DAY) as grp " +
                    "    FROM daily_plays " +
                    ") " +
                    "SELECT COALESCE(MAX(streak_length), 0) as max_streak " +
                    "FROM ( " +
                    "    SELECT COUNT(*) as streak_length " +
                    "    FROM date_groups " +
                    "    GROUP BY grp " +
                    ") streaks";

            try (PreparedStatement stmt = connection.prepareStatement(streakQuery)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        mapped.addColumn("longestStreak", rs.getInt("max_streak"));
                    } else {
                        mapped.addColumn("longestStreak", 0);
                    }
                }
            }

            // Get current year
            String yearQuery = "SELECT YEAR(CURDATE()) as current_year";
            try (PreparedStatement stmt = connection.prepareStatement(yearQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    mapped.addColumn("year", rs.getInt("current_year"));
                } else {
                    mapped.addColumn("year", 2025);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error generating MangoMusic Mapped", e);
        }

        return mapped;
    }

    private String determineListenerPersonality(int totalPlays, int uniqueArtists, int completedPlays) {
        if (totalPlays == 0) {
            return "New Explorer";
        }

        double completionRate = (double) completedPlays / totalPlays;
        double artistDiversity = (double) totalPlays / Math.max(uniqueArtists, 1);

        if (totalPlays > 500 && completionRate > 0.85) {
            return "Devoted Audiophile";
        } else if (totalPlays > 300 && uniqueArtists > 50) {
            return "Genre Wanderer";
        } else if (artistDiversity > 15 && completionRate > 0.75) {
            return "Loyal Fan";
        } else if (uniqueArtists > 40 && completionRate < 0.70) {
            return "Playlist Skipper";
        } else if (totalPlays > 400) {
            return "Music Enthusiast";
        } else if (uniqueArtists < 10) {
            return "True Believer";
        } else if (completionRate > 0.80) {
            return "Patient Listener";
        } else if (totalPlays > 200) {
            return "Casual Curator";
        } else {
            return "Emerging Fan";
        }
    }
}