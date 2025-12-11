package com.mangomusic.model;

public class Album {

    private Integer albumId;  // Changed from int
    private Integer artistId;  // Changed from int
    private String title;
    private Integer releaseYear;  // Changed from int
    private String artistName;

    public Album() {
    }

    public Album(Integer albumId, Integer artistId, String title, Integer releaseYear, String artistName) {
        this.albumId = albumId;
        this.artistId = artistId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.artistName = artistName;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public String toString() {
        return "Album{" +
                "albumId=" + albumId +
                ", artistId=" + artistId +
                ", title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                ", artistName='" + artistName + '\'' +
                '}';
    }
}