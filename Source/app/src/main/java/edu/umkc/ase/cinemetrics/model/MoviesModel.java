package edu.umkc.ase.cinemetrics.model;

/**
 * Created by vinuthna on 14-02-2017.
 */

public class MoviesModel {

    private String movieName;
    private String poster;
    private String releaseDate;
    private String posterId;
    private String genre;

    public MoviesModel(String movieName, String posterId, String genre) {
        this.movieName = movieName;
        this.genre = genre;
        this.posterId = "https://image.tmdb.org/t/p/w500/"+posterId;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getPoster() {
        return poster;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setPoster(String poster) {
        this.poster = poster;
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
}
