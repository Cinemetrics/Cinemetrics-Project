package edu.umkc.ase.cinemetrics.model;

/**
 * Created by vinuthna on 14-02-2017.
 */

public class MoviesModel {
    private String movieName;
    private String releaseDate;
    private String posterId;
    private String genre;
    private String language;


    public MoviesModel(String movieName, String genre, String posterId, String releaseDate, String language) {
        this.movieName = movieName;
        this.genre = genre;
        if(!posterId.contains("https"))
            this.posterId = "https://image.tmdb.org/t/p/w500/"+posterId;
        else
            this.posterId = posterId;
        this.releaseDate = releaseDate;
        this.language = language;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
