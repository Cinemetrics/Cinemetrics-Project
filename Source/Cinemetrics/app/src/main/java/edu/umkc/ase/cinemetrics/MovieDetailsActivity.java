package edu.umkc.ase.cinemetrics;

import android.Manifest;
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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.umkc.ase.cinemetrics.model.MoviesModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.facebook.FacebookSdk;

import com.google.android.gms.maps.model.LatLng;

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
    TextView lblTagline;
    TextView txtTagline;
    TextView lblBudget;
    TextView txtBudget;
    TextView lblLanguage;
    TextView txtLanguage;
    TextView lblCast;
    TextView txtCast;
    TextView lblHomepage;
    TextView txtHomepage;
    TextView lblRuntime;
    TextView txtRuntime;
    TextView lbl_nearestTheaters;
    TextView txt_nearestTheaters;
   // TextView lblYoutube;
    String[] genereListUpdated;
    String[] movieDetails;
    ImageView youTubeLink;
    RatingBar ratingBar;
    public static String videoIdNumber = "";
    RecyclerView recyclerView;
    String language = "";

    ToggleButton reminderButton;

    public final static String TAG = "tag";
    public final static String Genre = "tag";
    public final static String Movie = "tag";

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
    public Geocoder geocoder;
    public String zipCode= "";

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
        txtRating = (TextView) findViewById(R.id.txt_rating);
        txtGenre = (TextView) findViewById(R.id.txt_genre);
        lblPlot = (TextView) findViewById(R.id.lbl_plot);
        lblReleaseDate = (TextView) findViewById(R.id.lbl_releaseDate);
        txtReleaseDate = (TextView) findViewById(R.id.txt_releaseDate);
        lblRating = (TextView) findViewById(R.id.lbl_rating);
        lblGenre = (TextView) findViewById(R.id.lbl_Genre);
        lblTagline = (TextView) findViewById(R.id.lbl_tagline);
        txtTagline = (TextView) findViewById(R.id.txt_tagline);
        lblBudget = (TextView) findViewById(R.id.lbl_budget);
        txtBudget = (TextView) findViewById(R.id.txt_budget);
        lblLanguage = (TextView) findViewById(R.id.lbl_language);
        txtLanguage = (TextView) findViewById(R.id.txt_language);
        lblRuntime = (TextView) findViewById(R.id.lbl_runTime);
        txtRuntime = (TextView) findViewById(R.id.txt_runTime);
        lblCast = (TextView) findViewById(R.id.lbl_cast);
        txtHomepage = (TextView) findViewById(R.id.txt_homepage);
        lblHomepage = (TextView) findViewById(R.id.lbl_homepage);
        txtCast = (TextView) findViewById(R.id.txt_cast);
        lbl_nearestTheaters = (TextView) findViewById(R.id.lbl_nearestTheaters);
        txt_nearestTheaters = (TextView) findViewById(R.id.txt_nearestTheaters);
        youTubeLink = (ImageView) findViewById(R.id.img_youtubelink);
       // lblYoutube = (TextView) findViewById(R.id.lbl_youtube);
        ratingBar = (RatingBar) findViewById(R.id.pop_ratingbar);
        reminderButton = (ToggleButton) findViewById(R.id.remainderbtn) ;
        mContext = getApplicationContext();

            geocoder = new Geocoder(this);
            StringBuilder userAddress = new StringBuilder();
            LocationManager userCurrentLocation = (LocationManager) this
                    .getSystemService(Context.LOCATION_SERVICE);
            LocationListener userCurrentLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            LatLng userCurrentLocationCorodinates = null;
            double latitute = 0, longitude = 0;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //show message or ask permissions from the user.
                Toast.makeText(getBaseContext(),"Please turn on your location!", Toast.LENGTH_SHORT).show();
                return;
            }
            //Getting the current location of the user.
            userCurrentLocation.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, 0, userCurrentLocationListener);
            latitute = userCurrentLocation
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    .getLatitude();
            longitude = userCurrentLocation
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    .getLongitude();
            userCurrentLocationCorodinates = new LatLng(latitute,longitude);
            try {
                List<Address> addresses = geocoder.getFromLocation(latitute, longitude, 1);
                zipCode = addresses.get(0).getPostalCode();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
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
                reminderButton.setChecked(true);
            }
            else
            {
                reminderButton.setChecked(false);
            }
        }
        else
        {
            reminderButton.setChecked(false);
        }



        reminderButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
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

    public void backToSearch(View view){
        Intent redirect = new Intent(MovieDetailsActivity.this, SearchActivity.class);
        redirect.putExtra("userModelClass", SearchActivity.userModel);
        startActivity(redirect);
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
                            String date = object.getString("release_date");
                            String[] dateParams = date.split("-");
                            date = dateParams[1] + "-" + dateParams[2] + "-" +dateParams[0];
                            final String releaseDate = date;
                            final String genre = object.getString("genre_ids").substring(1,object.getString("genre_ids").length()-1);
                            movieId = object.getString("id");
                            final String[] genreList = genre.split(",");
                            final String rating = object.getString("vote_average");
                            final String language = object.getString("original_language").equals("en")?"English":"Not specified";
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
                                    FetchMoreDetails(movieId);
                                    return null;
                                }
                            }.execute();
                            OkHttpUtils.cancelCallWithTag(client, Genre);
                            new AsyncTask() {
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    FetchTheaters(movieName);
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
                                    lblLanguage.setText("Language:");
                                    (txtLanguage).setText(language);
                                    ratingBar.setRating(Float.parseFloat(rating));///2
                                    youTubeLink.setVisibility(View.VISIBLE);
                                   // lblYoutube.setText("Trailer");
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
        final StringBuilder genre = new StringBuilder();

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
                           // genreList[j] = convertedTextArray.getJSONObject(i).getString("name");
                            genre.append(convertedTextArray.getJSONObject(i).getString("name")).append(",");
                        }
                    }
                }
                genre.deleteCharAt(genre.length() - 1);
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
                (txtGenre).setText(genre.toString());
            }});
        return genreList;
    }

    public void FetchMoreDetails(final String movieId)
    {
        String[] castList = null;
        String cast = "";

        //final String[] genreListIn = genreList;
//        String genreUrl = "https://api.themoviedb.org/3/genre/movie/list?language=en-US&api_key=cefdee46a223603265c8a1ef40bd20d6";

        String movieDetailsUrl = "https://api.themoviedb.org/3/movie/"
                    +movieId +"?api_key=cefdee46a223603265c8a1ef40bd20d6&append_to_response=credits";
        OkHttpClient client=new OkHttpClient();
        Request newRequest=new Request.Builder()
                .url(movieDetailsUrl)
                .tag(Movie)
                .build();

        try {
            Response response=client.newCall(newRequest).execute();
            final JSONObject jsonResult;
            final String result = response.body().string();
            try
            {
                jsonResult = new JSONObject(result);

                final String tagline = jsonResult.getString("tagline");
                final String budget = jsonResult.getString("budget");
//                if(jsonResult.getString("original_language").equals("en")) {
//                    final String language = "english";
//                }
//                else
//                {
//                    final String language = "Not specified";
//                }

                //JSONArray convertedTextArray = jsonResult.getJSONArray("credits");
                JSONObject object = jsonResult.getJSONObject("credits");
                JSONArray conJsonArray = object.getJSONArray("cast");

                for (int i = 0; i < 5; i++)
                {
                    if(i==0) {
                        cast = conJsonArray.getJSONObject(i).getString("name");
                    }
                    else
                    {cast = cast + ", " + conJsonArray.getJSONObject(i).getString("name");}
                }
                final String movieCast = cast;
                final String movieHomepage = jsonResult.getString("homepage");
                String rumtime = jsonResult.getString("runtime");
                int hours = (Integer.parseInt(rumtime)) / 60; //since both are ints, you get an int
                int minutes = (Integer.parseInt(rumtime)) % 60;
                final String runTime = hours + "h " +minutes +"m";

                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        lblTagline.setText("Movie Tagline:");
                        (txtTagline).setText(tagline);
                        lblRuntime.setText("Movie Runtime:");
                        (txtRuntime).setText(runTime);
                        lblBudget.setText("Movie Budget:");
                        (txtBudget).setText("$ " +budget);
                        lblCast.setText("Cast:");
                        (txtCast).setText(movieCast);
                        lblHomepage.setText("Movie Homepage:");
                        (txtHomepage).setText(movieHomepage);
                    }});
            }
            catch (JSONException ex)
            {
                ex.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Http3","ran into exception: "+e.getMessage());
        }

//        return movieDetails;
    }

    public void FetchTheaters(final String movieName)
    {
        //String[] theaterList = null;
        final StringBuilder theater = new StringBuilder();
        Date today = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        String date = DATE_FORMAT.format(today);

         String movieDetailsUrl = "http://data.tmsapi.com/v1.1/movies/showings?startDate="
                //+date +"&zip=66062&api_key=j3hfvhnnhwgwjx3n8wtwbx99";
                 +date +"&zip=" + zipCode +"&api_key=j3hfvhnnhwgwjx3n8wtwbx99";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(movieDetailsUrl)
                .tag(Genre)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final JSONArray jsonResult;
                final String result = response.body().string();
                ArrayList<String> movieList = new ArrayList<String>();
                try {
                    jsonResult = new JSONArray(result);


                    for(int count=0;count<jsonResult.length();count++){
                        JSONObject jsonObj = (JSONObject) jsonResult.get(count);
                        if(movieName.equalsIgnoreCase(jsonObj.get("title").toString())){
                            JSONArray jarray = jsonObj.getJSONArray("showtimes");
                            for(int count1=0;count1<jarray.length();count1++){
                                JSONObject jsonObj1 = jarray.getJSONObject(count1);

                                if(jsonObj1.has("theatre") && jsonObj1.has("ticketURI")) {
                                    JSONObject job = jsonObj1.getJSONObject("theatre");

                                    if(!theater.toString().contains(job.get("name").toString())) {
                                        theater.append(job.get("name").toString()).append(" : ").append(jsonObj1.get("ticketURI").toString()).append("\n\n");
                                    }
                                    else {
                                        //do nothing
                                    }
                                }
                            }
                        }
                    }

                   // if(theater.length()>0)
                     //   theater.deleteCharAt(theater.length() - 1);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            lbl_nearestTheaters.setText("Nearest Movie Theaters:");
                            txt_nearestTheaters.setText(theater.toString());
                        }});

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



//        return movieDetails;
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
        extras.putString("userName", userName);
        extras.putString("alarm", "on");
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void DeleteAlarm(int alarmId)
    {
        Intent intent = new Intent(this, AlarmActivity.class);
        Bundle extras = new Bundle();
        extras.putString("moviename", movieName); //movie name
        extras.putString("userName", userName);
        extras.putString("alarm", "off");
        extras.putInt("id", alarmId);
        intent.putExtras(extras);
        startActivity(intent);
//        AlarmActivity alarmActivity = new AlarmActivity();
//        alarmActivity.deleteAlarm(alarmId);
    }
    //alarm
}
