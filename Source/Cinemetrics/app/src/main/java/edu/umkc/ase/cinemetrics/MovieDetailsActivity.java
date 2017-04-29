package edu.umkc.ase.cinemetrics;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import android.net.Uri;
import android.widget.ToggleButton;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.squareup.picasso.Picasso;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.facebook.FacebookSdk;

public class MovieDetailsActivity extends YouTubeBaseActivity {

    private PopupWindow mPopupWindow;
    private Context mContext;

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer.OnInitializedListener onInitializedListner;
    private String youTubeApi = "AIzaSyAfXROrTdgS3t4UujIdYZSTfrxfF6f_R1w";

    private String tmdbUrl = "https://api.themoviedb.org/3/search/movie?query=";
    private String movieName = "";
    private String userName = null;
    private String apiKey = "&api_key=cefdee46a223603265c8a1ef40bd20d6";

    ScrollView scrollView;
    ImageView moviePoster;
    ImageView internalMoviePoster;
    TextView txtTitle;
    TextView lblPlot;
    TextView txtOverview;
    TextView lblReleaseDate;
    TextView txtReleaseDate;
    TextView lblRating;
    TextView txtRating;
    TextView lblGenre;
    TextView txtGenre;
    TextView lblYoutube;
    String[] genereListUpdated;
    ImageView youTubeLink;
    RatingBar ratingBar;
    public static String videoIdNumber = "";
    RecyclerView recyclerView;
    String language = "";

    Switch sButton;

    public final static String TAG = "tag";
    public final static String Genre = "tag";

    String movieId = null;

    Toolbar toolbar;
    int initialTopPosition =0;

    //facebook
    ImageView share;
    private CallbackManager callbackManager;
    private LoginManager loginManager;

    //DB
    MongoClientURI uri = null;
    MongoClient client = null;
    MongoDatabase db = null;

    //Alarm

    int alarmId = 0;
    //Alarm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //facebook
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_movie_details);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        java.security.Security.addProvider(new gnu.javax.crypto.jce.GnuSasl());
        uri  = new MongoClientURI("mongodb://moviedb:moviedb@ds155820.mlab.com:55820/movies_collection");
        client = new MongoClient(uri);
        db = client.getDatabase(uri.getDatabase());

        //Initialising page contents
        scrollView =(ScrollView)findViewById(R.id.scroll_form);
        moviePoster = (ImageView) findViewById(R.id.img_poster);
        internalMoviePoster = (ImageView) findViewById(R.id.internal_img_poster);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtOverview = (TextView) findViewById(R.id.txt_overview);
        txtReleaseDate = (TextView) findViewById(R.id.txt_releaseDate);
        txtRating = (TextView) findViewById(R.id.txt_rating);
        txtGenre = (TextView) findViewById(R.id.txt_genre);
        lblPlot = (TextView) findViewById(R.id.lbl_plot);
        lblReleaseDate = (TextView) findViewById(R.id.lbl_releaseDate);
        lblRating = (TextView) findViewById(R.id.lbl_rating);
        lblGenre = (TextView) findViewById(R.id.lbl_Genre);
        youTubeLink = (ImageView) findViewById(R.id.img_youtubelink);
        lblYoutube = (TextView) findViewById(R.id.lbl_youtube);
        ratingBar = (RatingBar) findViewById(R.id.pop_ratingbar);
        sButton = (Switch) findViewById(R.id.switch_btn);
        mContext = getApplicationContext();

        share = (ImageView) findViewById(R.id.share);
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
        };

        Bundle bundle = getIntent().getExtras();
       movieName = bundle.getString("movieName");
        userName = bundle.getString("userName");


        if(bundle.getString("activity") != null) {
            String activity = bundle.getString("activity");
            alarmId = bundle.getInt("_id");
            if(activity.equals("alarm")) {
                sButton.setChecked(true);
            }
            else
            {sButton.setChecked(false);}
        }
        else
        {sButton.setChecked(false);}




        //Set a CheckedChange Listener for Switch Button
        sButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean on){
                if(on)
                {
                    //Do something when Switch button is on/checked
                    OnSetAlarm();
                }
                else
                {
                    //Do nothing
                    DeleteAlarm(alarmId);
                }
            }
        });

        FetchMovieDetails();

        if(userName!=null) {

            final ToggleButton toggle = (ToggleButton) findViewById(R.id.watchtoggleButton);

            MongoCollection<Document> moviesCollection = db.getCollection("watched_movies");


            Document findQuery = new Document("movieTitle", movieName).append("username",userName);


            MongoCursor<Document> cursor = moviesCollection.find(findQuery).iterator();

            boolean recordFound = false;

            if (cursor != null & cursor.hasNext())
                recordFound = true;

            toggle.setChecked(recordFound);

            cursor.close();

            toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MongoCollection<Document> moviesCollection = db.getCollection("watched_movies");


                    Document movie = new Document("username", userName).append("movieTitle", movieName)
                            .append("moviePoster", moviePoster).append("genre", txtGenre.getText()).append("language", language).append("releaseDate", txtReleaseDate.getText());

                    if (toggle.isChecked()) {
                        moviesCollection.insertOne(movie);
                    } else {
                        moviesCollection.deleteOne(movie);
                        toggle.setChecked(false);
                    }
                }
            });

            //to watch
            final ToggleButton towatchtoggle = (ToggleButton)findViewById(R.id.towatchtoggleButton);

            MongoCollection<Document> towatchmoviesCollection = db.getCollection("towatch_movies");


            Document towatchfindQuery = new Document("movieTitle", movieName).append("username",userName);


            MongoCursor<Document> towatchcursor = towatchmoviesCollection.find(towatchfindQuery).iterator();

            boolean towatchrecordFound = false;

            if (towatchcursor != null & towatchcursor.hasNext())
                towatchrecordFound = true;

            towatchtoggle.setChecked(towatchrecordFound);

            towatchcursor.close();

            towatchtoggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MongoCollection<Document> towatchmoviesCollection = db.getCollection("towatch_movies");


                    Document movie = new Document("username", userName).append("movieTitle", movieName)
                            .append("moviePoster", moviePoster).append("genre", txtGenre.getText()).append("language", language).append("releaseDate", txtReleaseDate.getText());

                    if (towatchtoggle.isChecked()) {
                        towatchmoviesCollection.insertOne(movie);
                    } else {
                        towatchmoviesCollection.deleteOne(movie);
                        towatchtoggle.setChecked(false);
                    }
                }
            });

            //favorite
            final ToggleButton favtoggle = (ToggleButton)findViewById(R.id.favtoggleButton);

            MongoCollection<Document> favmoviesCollection = db.getCollection("fav_movies");


            Document favfindQuery = new Document("movieTitle", movieName).append("username",userName);


            MongoCursor<Document> favcursor = favmoviesCollection.find(favfindQuery).iterator();

            boolean favrecordFound = false;

            if (favcursor != null & favcursor.hasNext())
                favrecordFound = true;

            favtoggle.setChecked(favrecordFound);

            favcursor.close();

            favtoggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MongoCollection<Document> favmoviesCollection = db.getCollection("fav_movies");


                    Document movie = new Document("username", userName).append("movieTitle", movieName)
                            .append("moviePoster", moviePoster).append("genre", txtGenre.getText()).append("language", language).append("releaseDate", txtReleaseDate.getText());

                    if (favtoggle.isChecked()) {
                        favmoviesCollection.insertOne(movie);
                    } else {
                        favmoviesCollection.deleteOne(movie);
                        favtoggle.setChecked(false);
                    }
                }
            });

        }else{
            findViewById(R.id.watchtoggleButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.towatchtoggleButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.favtoggleButton).setVisibility(View.INVISIBLE);
        }
    }

    public void shareOnSocialMedia(View view)
    {
        String appName = "Cinemetrics Application \n" ;
        String data =  "https://www.themoviedb.org/movie/" +movieId;

        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
        if (waIntent != null) {
            waIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    appName + data);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } else
            Toast.makeText(this, "Unable to post. Please try after sometime!", Toast.LENGTH_SHORT)
                    .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
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
                            String internalPstrUrl ="";
                            if(convertedTextArray.length()>1)
                                internalPstrUrl = "https://image.tmdb.org/t/p/w500" + convertedTextArray.getJSONObject(1).getString("poster_path");
                            else
                                internalPstrUrl = posterUrl;
                            final String internalPosterUrl = internalPstrUrl;
                            final String title = object.getString("title").toString();
                            final String description = object.getString("overview");
                            final String releaseDate = object.getString("release_date");
                            final String genre = object.getString("genre_ids").substring(1,object.getString("genre_ids").length()-1);
                            movieId = object.getString("id");
                            language = object.get("original_language").toString();
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
                                            .load(posterUrl).into(moviePoster);
                                    Picasso.with(internalMoviePoster.getContext())
                                            .load(internalPosterUrl).into(internalMoviePoster);
                                    (txtTitle).setText(title);
                                    lblPlot.setText("Overview:");
                                    (txtOverview).setText(description);
                                    lblReleaseDate.setText("Release Date:");
                                    (txtReleaseDate).setText(releaseDate);
                                    lblRating.setText("Rating:");
                                    (txtRating).setText(rating);
                                    ratingBar.setRating(Float.parseFloat(rating));///2
                                    youTubeLink.setVisibility(View.VISIBLE);
                                    lblYoutube.setText("Trailer");
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

        }
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
                + movieName + " - trailer" + "&type=video&key=AIzaSyAfXROrTdgS3t4UujIdYZSTfrxfF6f_R1w";
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
            scrollView.scrollTo(0, youTubePlayerView.getBottom());
            youTubeLink.setVisibility(View.INVISIBLE);
            //playing in aap - original
//            youTubePlayerView.setVisibility(View.VISIBLE);
            youTubePlayerView.initialize(youTubeApi, onInitializedListner);

            //to pass control to youtube page
//            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.youtube.com/watch?v="+videoIdNumber)));
        }
        else
        {
            Toast.makeText(getBaseContext(), "No trailer to show!", Toast.LENGTH_LONG).show();
        }
    }
    public void YouTubeNavigation(View view)
    {
        if(videoIdNumber!= null) {
            scrollView.scrollTo(0, youTubePlayerView.getBottom());
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.youtube.com/watch?v="+videoIdNumber)));
        }
        else
        {
            Toast.makeText(getBaseContext(), "Try again later!", Toast.LENGTH_LONG).show();
        }
    }

    //alarm
    public void OnSetAlarm() {
        Intent intent = new Intent(this, AlarmActivity.class);
        Bundle extras = new Bundle();
        extras.putString("moviename", movieName); //movie name
        extras.putString("alarm", "on");
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void DeleteAlarm(int alarmId)
    {
        Intent intent = new Intent(this, AlarmActivity.class);
        Bundle extras = new Bundle();
        extras.putString("moviename", movieName); //movie name
        extras.putString("alarm", "off");
        extras.putInt("id", alarmId);
        intent.putExtras(extras);
        startActivity(intent);
//        AlarmActivity alarmActivity = new AlarmActivity();
//        alarmActivity.deleteAlarm(alarmId);
    }
    //alarm
}
