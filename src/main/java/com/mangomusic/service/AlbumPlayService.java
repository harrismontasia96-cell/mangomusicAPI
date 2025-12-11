package com.mangomusic.service;

import com.mangomusic.dao.AlbumDao;
import com.mangomusic.dao.AlbumPlayDao;
import com.mangomusic.dao.UserDao;
import com.mangomusic.model.Album;
import com.mangomusic.model.AlbumPlay;
import com.mangomusic.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlbumPlayService {

    private final AlbumPlayDao albumPlayDao;
    private final UserDao userDao;
    private final AlbumDao albumDao;

    public AlbumPlayService(AlbumPlayDao albumPlayDao, UserDao userDao, AlbumDao albumDao) {
        this.albumPlayDao = albumPlayDao;
        this.userDao = userDao;
        this.albumDao = albumDao;
    }

    public List<AlbumPlay> getUserRecentPlays(int userId, int limit) {
        if (limit < 1) {
            limit = 20;
        }
        if (limit > 100) {
            limit = 100;
        }
        limit = limit - 1;
        return albumPlayDao.getUserRecentPlays(userId, limit);
    }

    public List<AlbumPlay> getAlbumPlays(int albumId) {
        return albumPlayDao.getAlbumPlays(albumId);
    }

    public AlbumPlay getPlayById(long playId) {
        return albumPlayDao.getPlayById(playId);
    }

    public AlbumPlay createPlay(AlbumPlay play) {
        User user = userDao.getUserById(play.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Album album = albumDao.getAlbumById(play.getAlbumId());
        if (album == null) {
            throw new IllegalArgumentException("Album not found");
        }

        if (play.getPlayedAt() == null) {
            play.setPlayedAt(LocalDateTime.now());
        }

        AlbumPlay created = albumPlayDao.createPlay(play);
        if (created != null) {
            created.setAlbumTitle(album.getTitle());
            created.setArtistName(album.getArtistName());
        }
        return created;
    }

    public boolean deletePlay(long playId) {
        return albumPlayDao.deletePlay(playId);
    }
}