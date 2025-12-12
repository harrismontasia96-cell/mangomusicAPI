package com.mangomusic.service;

import com.mangomusic.dao.AlbumDao;
import com.mangomusic.dao.ArtistDao;
import com.mangomusic.dao.AlbumPlayDao;
import com.mangomusic.model.Album;
import com.mangomusic.model.AlbumPlay;
import com.mangomusic.model.Artist;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class AlbumService {

    private final AlbumDao albumDao;
    private final ArtistDao artistDao;
    private final AlbumPlayDao albumPlayDao;

    public AlbumService(AlbumDao albumDao, ArtistDao artistDao, AlbumPlayDao albumPlayDao) {
        this.albumDao = albumDao;
        this.artistDao = artistDao;
        this.albumPlayDao = albumPlayDao;
    }

    public List<Album> getAllAlbums() {
        return albumDao.getAllAlbums();
    }
    public List<Map<String, Object>> getTrendingAlbums(int days) {

        if (days < 1) days = 1;
        if (days > 30) days = 30;

        return albumPlayDao.getTrendingAlbums(days);
    }

    public Album getAlbumById(int albumId) {
        return albumDao.getAlbumById(albumId);
    }

    public List<Album> getAlbumsByArtist(int artistId) {
        return albumDao.getAlbumsByArtist(artistId);
    }

    public List<Album> getAlbumsByGenre(String genre) {
        return albumDao.getAlbumsByGenre(genre);
    }


    public Map<String, Object> getAlbumPlayCount(int albumId) {


        Album album = albumDao.getAlbumById(albumId);
        if (album == null) {
            return null;
        }


        Map<String, Object> dbResult = albumPlayDao.getAlbumPlayCount(albumId);


        if (dbResult == null) {
            dbResult = new HashMap<>();
            dbResult.put("albumId", albumId);
            dbResult.put("albumTitle", album.getTitle());
            dbResult.put("artistName", album.getArtistName());
            dbResult.put("playCount", 0);
        }

        return dbResult;
    }

    public void incrementPlayCount(int albumId) {
        Album album = albumDao.getAlbumById(albumId);
        if (album == null) {
            throw new RuntimeException("Album not found");
        }
        AlbumPlay play = new AlbumPlay();
        play.setAlbumId(albumId);
        play.setUserId(1);
        play.setCompleted(false);
        play.setPlayedAt(java.time.LocalDateTime.now());

        albumPlayDao.createPlay(new AlbumPlay());
    }

    public List<Album> searchAlbums(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllAlbums();
        }
        return albumDao.searchAlbums(searchTerm);
    }

    public Album createAlbum(Album album) {
        validateAlbum(album);

        Artist artist = artistDao.getArtistById(album.getArtistId());
        if (artist == null) {
            throw new IllegalArgumentException("Artist not found");
        }

        Album created = albumDao.createAlbum(album);
        if (created != null) {
            created.setArtistName(artist.getName());
        }
        return created;
    }

    public Album updateAlbum(int albumId, Album album) {
        validateAlbum(album);

        Artist artist = artistDao.getArtistById(album.getArtistId());
        if (artist == null) {
            throw new IllegalArgumentException("Artist not found");
        }

        Album updated = albumDao.updateAlbum(albumId, album);
        if (updated != null) {
            updated.setArtistName(artist.getName());
        }
        return updated;
    }

    public boolean deleteAlbum(int albumId) {
        return albumDao.deleteAlbum(albumId);
    }

    private void validateAlbum(Album album) {
        if (album.getTitle() == null || album.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Album title is required");
        }

        if (album.getArtistId() == null || album.getArtistId() <= 0) {
            throw new IllegalArgumentException("Valid artist ID is required");
        }

        if (album.getReleaseYear() != null) {
            if (album.getReleaseYear() < 1900 || album.getReleaseYear() > 2100) {
                throw new IllegalArgumentException("Release year must be between 1900 and 2100");
            }
        }
    }
}

