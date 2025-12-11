package com.mangomusic.model;

public class Artist {

    private Integer artistId;  // Changed from int to Integer
    private String name;
    private String primaryGenre;
    private Integer formedYear;

    public Artist() {
    }

    public Artist(Integer artistId, String name, String primaryGenre, Integer formedYear) {
        this.artistId = artistId;
        this.name = name;
        this.primaryGenre = primaryGenre;
        this.formedYear = formedYear;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryGenre() {
        return primaryGenre;
    }

    public void setPrimaryGenre(String primaryGenre) {
        this.primaryGenre = primaryGenre;
    }

    public Integer getFormedYear() {
        return formedYear;
    }

    public void setFormedYear(Integer formedYear) {
        this.formedYear = formedYear;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "artistId=" + artistId +
                ", name='" + name + '\'' +
                ", primaryGenre='" + primaryGenre + '\'' +
                ", formedYear=" + formedYear +
                '}';
    }
}