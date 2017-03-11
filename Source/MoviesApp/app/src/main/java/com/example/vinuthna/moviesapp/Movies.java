package com.example.vinuthna.moviesapp;

import android.widget.ImageView;

/**
 * Created by vinuthna on 14-02-2017.
 */

public class Movies {

    private String movieName;
    private String poster;
    private String releaseDate;
    private String posterId;

    public Movies(String movieName,String posterId) {
        this.movieName = movieName;
        this.releaseDate = releaseDate;
        //this.posterImage = posterImage;
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
}
