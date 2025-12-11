package com.mangomusic.dao;

import com.mangomusic.model.Artist;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ArtistDao {

    private final DataSource dataSource;

    public ArtistDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Artist> getAllArtists() {
        List<Artist> artists = new ArrayList<>();
        String query = "SELECT artist_id, name, primary_genre, formed_year FROM artists ORDER BY name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                Artist artist = mapRowToArtist(results);
                artists.add(artist);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting all artists", e);
        }

        return artists;
    }

    public Artist getArtistById(int artistId) {
        String query = "SELECT artist_id, name, primary_genre, formed_year FROM artists WHERE artist_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, artistId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapRowToArtist(results);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting artist by ID", e);
        }

        return null;
    }

    public List<Artist> searchArtists(String searchTerm) {
        List<Artist> artists = new ArrayList<>();
        String query = "SELECT artist_id, name, primary_genre, formed_year FROM artists WHERE name LIKE ? ORDER BY name";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + searchTerm + "%");

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Artist artist = mapRowToArtist(results);
                    artists.add(artist);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error searching artists", e);
        }

        return artists;
    }

    public List<String> getAllGenres() {
        List<String> genres = new ArrayList<>();
        String query = "SELECT DISTINCT primary_genre FROM artists ORDER BY primary_genre";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                genres.add(results.getString("primary_genre"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting all genres", e);
        }

        return genres;
    }

    public Artist createArtist(Artist artist) {
        String query = "INSERT INTO artists (name, primary_genre, formed_year) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, artist.getName());
            statement.setString(2, artist.getPrimaryGenre());
            statement.setObject(3, artist.getFormedYear());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int artistId = keys.getInt(1);
                    return getArtistById(artistId);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating artist", e);
        }

        return null;
    }

    public Artist updateArtist(int artistId, Artist artist) {
        String query = "UPDATE artists SET name = ?, primary_genre = ?, formed_year = ? WHERE artist_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, artist.getName());
            statement.setString(2, artist.getPrimaryGenre());
            statement.setObject(3, artist.getFormedYear());
            statement.setInt(4, artistId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                return null;
            }

            return getArtistById(artistId);

        } catch (SQLException e) {
            throw new RuntimeException("Error updating artist", e);
        }
    }

    public boolean deleteArtist(int artistId) {
        String query = "DELETE FROM artists WHERE artist_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, artistId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting artist", e);
        }
    }

    private Artist mapRowToArtist(ResultSet results) throws SQLException {
        Artist artist = new Artist();
        artist.setArtistId(results.getInt("artist_id"));
        artist.setName(results.getString("name"));
        artist.setPrimaryGenre(results.getString("primary_genre"));

        Integer formedYear = results.getInt("formed_year");
        if (results.wasNull()) {
            formedYear = null;
        }
        artist.setFormedYear(formedYear);

        return artist;
    }
}