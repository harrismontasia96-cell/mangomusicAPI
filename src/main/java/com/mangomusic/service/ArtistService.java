package com.mangomusic.service;

import com.mangomusic.dao.ArtistDao;
import com.mangomusic.model.Artist;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistService {

    private final ArtistDao artistDao;

    public ArtistService(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

    public List<Artist> getAllArtists() {
        return artistDao.getAllArtists();
    }

    public Artist getArtistById(int artistId) {
        return artistDao.getArtistById(artistId);
    }

    public List<Artist> searchArtists(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllArtists();
        }
        return artistDao.searchArtists(searchTerm);
    }

    public List<String> getAllGenres() {
        return artistDao.getAllGenres();
    }

    public Artist createArtist(Artist artist) {
        validateArtist(artist);
        return artistDao.createArtist(artist);
    }

    public Artist updateArtist(int artistId, Artist artist) {
        validateArtist(artist);
        return artistDao.updateArtist(artistId, artist);
    }

    public boolean deleteArtist(int artistId) {
        return artistDao.deleteArtist(artistId);
    }

    private void validateArtist(Artist artist) {
        if (artist.getName() == null || artist.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Artist name is required");
        }

        if (artist.getPrimaryGenre() == null || artist.getPrimaryGenre().trim().isEmpty()) {
            throw new IllegalArgumentException("Primary genre is required");
        }
    }
}