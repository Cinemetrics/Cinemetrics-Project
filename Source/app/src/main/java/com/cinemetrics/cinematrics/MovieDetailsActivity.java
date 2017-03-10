package com.cinemetrics.cinematrics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.android.youtube.player.YouTubeBaseActivity;

public class MovieDetailsActivity extends YouTubeBaseActivity {//extends AppCompatActivity {

//    Button b;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer.OnInitializedListener onInitializedListner;
    private String youTubeApi = "AIzaSyAfXROrTdgS3t4UujIdYZSTfrxfF6f_R1w";

    private String tmdbUrl = "https://api.themoviedb.org/3/search/movie?query=";
    private String movieName = "";
    private String apiKey = "&api_key=cefdee46a223603265c8a1ef40bd20d6&limit=5";

    ImageView moviePoster;
    TextView lblTitle;
    TextView txtTitle;
    TextView lblPlot;
    TextView txtOverview;
    TextView lblReleaseDate;
    TextView txtReleaseDate;
    TextView lblRating;
    TextView txtRating;
    TextView lblGenre;
    TextView txtGenre;
    String[] genereListUpdated;
    ImageView youTubeLink;
    public static String videoIdNumber = "";

    public final static String TAG = "tag";
    public final static String Genre = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //Initialising page contents
        moviePoster = (ImageView) findViewById(R.id.img_poster);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtOverview = (TextView) findViewById(R.id.txt_overview);
        txtReleaseDate = (TextView) findViewById(R.id.txt_releaseDate);
        txtRating = (TextView) findViewById(R.id.txt_rating);
        txtGenre = (TextView) findViewById(R.id.txt_genre);
        lblTitle = (TextView) findViewById(R.id.lbl_title);
        lblPlot = (TextView) findViewById(R.id.lbl_plot);
        lblReleaseDate = (TextView) findViewById(R.id.lbl_releaseDate);
        lblRating = (TextView) findViewById(R.id.lbl_rating);
        lblGenre = (TextView) findViewById(R.id.lbl_Genre);
        youTubeLink = (ImageView) findViewById(R.id.img_youtubelink);

        //added
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        onInitializedListner = new YouTubePlayer.OnInitializedListener()
        {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b)
            {
                youTubePlayer.loadVideo(MovieDetailsActivity.videoIdNumber);
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult)
            {}
        }; //added

        if(MainActivity.movieName != null)
        {
            movieName = "captain america";//MainActivity.movieName; //To be passed from another activity
        }
            FetchMovieDetails();
    }

    public void FetchMovieDetails()
    {

        String url = tmdbUrl + movieName + apiKey;
        if(movieName != null)
        {
            final OkHttpClient client = new OkHttpClient();
            try
            {
                final Request request = new Request.Builder()
                        .url(url)
                        .tag(TAG)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback()
                {
                    @Override
                    public void onFailure(Call call, IOException e)
                    {
                        System.out.println(e.getMessage());
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException
                    {
                        final JSONObject jsonResult;
                        final String result = response.body().string();
                        try
                        {
                            jsonResult = new JSONObject(result);
                            JSONArray convertedTextArray = jsonResult.getJSONArray("results");
                            JSONObject object = convertedTextArray.getJSONObject(0);
                            final String posterUrl = "https://image.tmdb.org/t/p/w500" + object.getString("poster_path");
                            final String title = object.getString("title").toString();
                            final String description = object.getString("overview");
                            final String releaseDate = object.getString("release_date");
                            final String genre = object.getString("genre_ids").substring(1,object.getString("genre_ids").length()-1);
                            final String[] genreList = genre.split(",");
                            final String rating = object.getString("vote_average");

                            //setting it with complete movie title
                            movieName = title;

                            OkHttpUtils.cancelCallWithTag(client, TAG);

                            new AsyncTask() {
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    genereListUpdated = FetchGenre(genreList);
                                    return null;
                                }
                            }.execute();

                            OkHttpUtils.cancelCallWithTag(client, Genre);

                            new AsyncTask() {
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    FetchVideoId(movieName);
                                    return null;
                                }
                            }.execute();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Picasso.with(moviePoster.getContext())
                                                .load(posterUrl)
                                                .resize(700, 200).into(moviePoster);
                                        lblTitle.setText("Title:");
                                        (txtTitle).setText(title);
                                        lblPlot.setText("Movie Plot:");
                                        (txtOverview).setText(description);
                                        lblReleaseDate.setText("Release Date:");
                                        (txtReleaseDate).setText(releaseDate);
                                        lblRating.setText("Rating:");
                                        (txtRating).setText(rating);
                                        youTubeLink.setVisibility(View.VISIBLE);
                                    }
                                });
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
            catch (Exception ex) //catch for try after okHttp
            {
                ex.printStackTrace();
            }
            //FetchGenre();

        }//if end
    }

    public String[] FetchGenre(final String[] genreList)
    {
        //final String[] genreListIn = genreList;
        String genreUrl = "https://api.themoviedb.org/3/genre/movie/list?language=en-US&api_key=cefdee46a223603265c8a1ef40bd20d6";

        OkHttpClient client=new OkHttpClient();
        Request newRequest=new Request.Builder()
                .url(genreUrl)
                .tag(Genre)
                .build();

        try {
            Response response=client.newCall(newRequest).execute();
            final JSONObject jsonResult;
            final String result = response.body().string();
            try {
                jsonResult = new JSONObject(result);
                JSONArray convertedTextArray = jsonResult.getJSONArray("genres");
                for (int i = 0; i < convertedTextArray.length(); i++)
                {
                    for (int j = 0; j < genreList.length; j++) {
                        if (convertedTextArray.getJSONObject(i).getString("id").equals(genreList[j]))
                        {
                            genreList[j] = convertedTextArray.getJSONObject(i).getString("name");
                        }
                    }
                }
            }
            catch (JSONException ex)
            {}

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Http3","ran into exception: "+e.getMessage());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                lblGenre.setText("Genre:");
                (txtGenre).setText(genreList[0]);
            }});
        return genreList;
    }

    public void FetchVideoId(String movieName)
    {
        String youTubeUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=10&q="
                + movieName + "&type=video&key=AIzaSyAfXROrTdgS3t4UujIdYZSTfrxfF6f_R1w";
        if (!movieName.isEmpty()) {
            final String response1 = "";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(youTubeUrl)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                final JSONObject jsonResult;
                final String result = response.body().string();
                try {
                    jsonResult = new JSONObject(result);
                    JSONArray convertedTextArray = jsonResult.getJSONArray("items");
                    JSONObject object = convertedTextArray.getJSONObject(0);
                    JSONObject videoDetails = object.getJSONObject("id");
                    JSONObject snippet = object.getJSONObject("snippet");
                    String videoId_value = videoDetails.getString("videoId");
                    videoIdNumber = videoId_value;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException ex) {
            }
        }
    }

    public void PlayTrailer(View view)
    {
        if(videoIdNumber!= null)
        {
//            Intent redirect = new Intent(MovieDetailsActivity.this, YouTubePlayerActivity.class);
//            startActivity(redirect);
            youTubePlayerView.setVisibility(View.VISIBLE);
            youTubePlayerView.initialize(youTubeApi, onInitializedListner);
        }
        else
        {
            Toast.makeText(getBaseContext(), "No trailer to show!", Toast.LENGTH_LONG).show();
        }
    }

}
