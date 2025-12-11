package com.mangomusic.model;

import java.time.LocalDateTime;

public class AlbumPlay {

    private Long playId;  // Changed from long to Long
    private Integer userId;  // Changed from int
    private Integer albumId;  // Changed from int
    private LocalDateTime playedAt;
    private Boolean completed;  // Changed from boolean to Boolean
    private String albumTitle;
    private String artistName;

    public AlbumPlay() {
    }

    public AlbumPlay(Long playId, Integer userId, Integer albumId, LocalDateTime playedAt,
                     Boolean completed, String albumTitle, String artistName) {
        this.playId = playId;
        this.userId = userId;
        this.albumId = albumId;
        this.playedAt = playedAt;
        this.completed = completed;
        this.albumTitle = albumTitle;
        this.artistName = artistName;
    }

    public Long getPlayId() {
        return playId;
    }

    public void setPlayId(Long playId) {
        this.playId = playId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public String toString() {
        return "AlbumPlay{" +
                "playId=" + playId +
                ", userId=" + userId +
                ", albumId=" + albumId +
                ", playedAt=" + playedAt +
                ", completed=" + completed +
                ", albumTitle='" + albumTitle + '\'' +
                ", artistName='" + artistName + '\'' +
                '}';
    }
}