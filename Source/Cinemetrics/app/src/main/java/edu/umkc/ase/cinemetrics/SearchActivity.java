package edu.umkc.ase.cinemetrics;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.umkc.ase.cinemetrics.managers.SharedPreferenceManager;
import edu.umkc.ase.cinemetrics.model.MoviesModel;
import edu.umkc.ase.cinemetrics.model.UserModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import  android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SearchActivity extends AppCompatActivity {

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    SimpleDraweeView simpleDraweeView;

    TextView nameTextView;

    TextView emailTextView;

    RecyclerView recyclerView;
    private MovieAdapter adapter;


    // Search EditText
    EditText inputSearch;


    final String API_URL = "https://api.themoviedb.org/3/discover/movie?";
    final String API_KEY = "api_key=606d616b503278cd9d123c76c7e0e15f";
    final String QUERY="&primary_release_year=2017&popularity>50&original_language=en";

    final String SEARCH_API_URL = "https://api.themoviedb.org/3/search/movie?";
    final String SEARCH_QUERY="&query=";
    final String GENRE_QUERY="https://api.themoviedb.org/3/genre/movie/list?";

    final String PERSON_QUERY="http://api.tmdb.org/3/search/person?";
    String sourceText;
    TextView outputTextView;
    ArrayList<MoviesModel> finalmovieList = new ArrayList<MoviesModel>();
    public static Map<String,String> genreMap = new HashMap<String,String>();

    MongoClientURI uri = null;
    MongoClient client = null;
    MongoDatabase db = null;

    //Speech to Text
    protected static final int RESULT_SPEECH = 1;
    private ImageView btnSpeak;
    //Speech to Text

    public static UserModel userModel = null;
    String userName = null;

    private SwipeRefreshLayout swipeContainer;

    public SearchActivity(){
        //Get genre

        String getGenreUrl= GENRE_QUERY+API_KEY;

        SearchActivity.GenreAsyncTaskRunner runner = new SearchActivity.GenreAsyncTaskRunner();

        runner.execute(getGenreUrl);

        java.security.Security.addProvider(new gnu.javax.crypto.jce.GnuSasl());
        uri  = new MongoClientURI("mongodb://moviedb:moviedb@ds155820.mlab.com:55820/movies_collection");
        client = new MongoClient(uri);
        db = client.getDatabase(uri.getDatabase());
    }

    private class GenreAsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            try {

               // Thread.sleep(2000);
                resp = "Slept";

                if(params!=null) {
                    OkHttpClient client0 = new OkHttpClient();
                    try {
                        Request request = new Request.Builder()
                                .url(params[0])
                                .build();
                        client0.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                System.out.println(e.getMessage());
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final JSONObject jsonResult;
                                final String result = response.body().string();

                                try {
                                    jsonResult = new JSONObject(result);
                                    JSONArray convertedTextArray = jsonResult.getJSONArray("genres");

                                    for(int count=0;count<convertedTextArray.length();count++){
                                        JSONObject resultObj = (JSONObject) convertedTextArray.get(count);
                                        final String id = resultObj.get("id").toString();
                                        final String name = resultObj.get("name").toString();
                                        genreMap.put(id,name);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }
                }

            } /*catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            }*/ catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {

        }


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
          ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.menu);
        ab.setDisplayHomeAsUpEnabled(true);

        userModel = getUserModelFromIntent();

        if(userModel!=null){
            hideItem(false);
            userName = userModel.userEmail;
        }else{
            userModel = (UserModel) getIntent().getParcelableExtra("userModelClass");

            if(userModel!=null){
                hideItem(false);
                userName = userModel.userEmail;
            }else{
                hideItem(true);
            }
        }

        //if(userModel!=null)
            setDataOnNavigationView(userModel);

        inputSearch = (EditText) findViewById(R.id.inputSearch);

        TextView movieCount = (TextView) findViewById(R.id.movieCount) ;

        if(userModel!=null) {

            movieCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptPieChart();
                }
            });

            MongoCollection<Document> moviesCollection = db.getCollection("watched_movies");

            // Document findQuery = new Document("username",userModel.userEmail);
            Document findQuery = new Document("username",userName);

            MongoCursor<Document> cursor = moviesCollection.find(findQuery).iterator();

            int count = 0;
            try {
                while (cursor.hasNext()) {
                    cursor.next();
                    count++;
                }
            } finally {
                cursor.close();
            }

            movieCount.setText("Watched Movies: " + String.valueOf(count));
        }else{
            movieCount.setVisibility(View.INVISIBLE);
        }

        // Listview Data
        String getURL = API_URL+API_KEY+QUERY;//The API service URL

        SearchActivity.MovieSearchAsyncTaskRunner runner = new SearchActivity.MovieSearchAsyncTaskRunner();

        runner.execute("getURL",getURL);

        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //MainActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

                if(arg0.length()>=5){

                    String getURL = SEARCH_API_URL+API_KEY+SEARCH_QUERY+arg0;//The API service URL

                    finalmovieList = new ArrayList<MoviesModel>();

                    String personUrl = PERSON_QUERY+API_KEY+SEARCH_QUERY+arg0;

                    SearchActivity.MovieSearchAsyncTaskRunner runner = new SearchActivity.MovieSearchAsyncTaskRunner();

                    runner.execute("searchURL",getURL,personUrl);
                }

            }
        });

        //Speech to Text
        btnSpeak = (ImageView) findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    inputSearch.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
        //Speech to Text

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



    }

    public void fetchTimelineAsync(int page) {

        Intent redirect = new Intent(SearchActivity.this, SearchActivity.class);
        redirect.putExtra("userModelClass", userModel);
        startActivity(redirect);
    }


    private class MovieSearchAsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            try {

                //Thread.sleep(2000);
                resp = "Slept";

                if(params!=null) {

                    if("getURL".equalsIgnoreCase(params[0])){

                        final String response1 = "";
                        OkHttpClient client = new OkHttpClient();
                        try {

                            Request request = new Request.Builder()
                                    .url(params[1])
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    System.out.println(e.getMessage());
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final JSONObject jsonResult;
                                    final String result = response.body().string();
                                    ArrayList<String> movieList = new ArrayList<String>();
                                    try {
                                        jsonResult = new JSONObject(result);
                                        JSONArray convertedTextArray = jsonResult.getJSONArray("results");

                                        for(int count=0;count<convertedTextArray.length();count++){
                                            JSONObject resultObj = (JSONObject) convertedTextArray.get(count);
                                            final String movieTitle = resultObj.get("title").toString();
                                            // Log.d("okHttp", jsonResult.toString());
                                            final String moviePoster = resultObj.get("poster_path").toString();
                                            final String[] genre = resultObj.get("genre_ids").toString().replace("[", "").replace("]", "").split(",");
                                            final String language = resultObj.get("original_language").toString();
                                            final String releaseDate = resultObj.get("release_date").toString();;

                                            String genreFields = "";
                                            for (String str : genre) {
                                                genreFields = genreFields + genreMap.get(str) + ",";
                                            }
                                            genreFields = genreFields.substring(0, genreFields.length() - 1);

                                            MoviesModel m = new MoviesModel(movieTitle, genreFields, moviePoster, releaseDate,language);

                                            finalmovieList.add(m);
                                        }






                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                navigatetoSearchField(finalmovieList);
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }else{
                        final String response1 = "";
                        OkHttpClient client = new OkHttpClient();
                        try {

                            Request request = new Request.Builder()
                                    .url(params[2])
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    System.out.println(e.getMessage());
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final JSONObject jsonResult;
                                    final String result = response.body().string();
                                    ArrayList<String> movieList = new ArrayList<String>();
                                    try {
                                        jsonResult = new JSONObject(result);
                                        JSONArray convertedTextArray1 = jsonResult.getJSONArray("results");


                                        for(int count1=0;count1<convertedTextArray1.length();count1++){

                                            JSONObject obj = (JSONObject) convertedTextArray1.get(count1);
                                            JSONArray convertedTextArray = obj.getJSONArray("known_for");

                                            for(int count=0;count<convertedTextArray.length();count++){
                                                JSONObject resultObj = (JSONObject) convertedTextArray.get(count);
                                                final String movieTitle = resultObj.get("title").toString();
                                                // Log.d("okHttp", jsonResult.toString());
                                                final String moviePoster = resultObj.get("poster_path").toString();
                                                final String[] genre = resultObj.get("genre_ids").toString().replace("[", "").replace("]", "").split(",");
                                                final String language = resultObj.get("original_language").toString();
                                                final String releaseDate = resultObj.get("release_date").toString();;

                                                String genreFields = "";
                                                for (String str : genre) {
                                                    genreFields = genreFields + genreMap.get(str) + ",";
                                                }
                                                genreFields = genreFields.substring(0, genreFields.length() - 1);

                                                MoviesModel m = new MoviesModel(movieTitle, genreFields, moviePoster, releaseDate,language);

                                                finalmovieList.add(m);
                                            }
                                        }


                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                navigatetoSearchField(finalmovieList);
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });



                            request = new Request.Builder()
                                    .url(params[1])
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    System.out.println(e.getMessage());
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final JSONObject jsonResult;
                                    final String result = response.body().string();
                                    ArrayList<String> movieList = new ArrayList<String>();
                                    try {
                                        jsonResult = new JSONObject(result);
                                        JSONArray convertedTextArray = jsonResult.getJSONArray("results");

                                        for(int count=0;count<convertedTextArray.length();count++){
                                            JSONObject resultObj = (JSONObject) convertedTextArray.get(count);
                                            final String movieTitle = resultObj.get("title").toString();
                                            // Log.d("okHttp", jsonResult.toString());
                                            final String moviePoster = resultObj.get("poster_path").toString();
                                            final String[] genre = resultObj.get("genre_ids").toString().replace("[", "").replace("]", "").split(",");
                                            final String language = resultObj.get("original_language").toString();
                                            final String releaseDate = resultObj.get("release_date").toString();;

                                            String genreFields = "";
                                            for (String str : genre) {
                                                genreFields = genreFields + genreMap.get(str) + ",";
                                            }
                                            genreFields = genreFields.substring(0, genreFields.length() - 1);

                                            MoviesModel m = new MoviesModel(movieTitle, genreFields, moviePoster, releaseDate,language);

                                            finalmovieList.add(m);
                                        }


                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                navigatetoSearchField(finalmovieList);
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }
                    }


                }

            } /*catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            }*/ catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {

        }


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected void onProgressUpdate(String... text) {


        }
    }

    private void attemptPieChart() {

        Intent redirect = new Intent(SearchActivity.this, PieMainActivity.class);
        redirect.putExtra("userModelClass", userModel);
        startActivity(redirect);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_another, menu);
        return true;
    }

    private void navigatetoSearchField(ArrayList<MoviesModel> finalmovieList1) {

        ProgressBar spinner;

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        adapter = new MovieAdapter(finalmovieList1, getApplication(),userName);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(spinner!=null){

            spinner.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSignOut(View view) {
        finish();
    }

    private UserModel getUserModelFromIntent()
    {
        Intent intent = getIntent();
        return intent.getParcelableExtra(UserModel.class.getSimpleName());
    }

    private void startLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void setDataOnNavigationView(UserModel userModel) {
        if (navigationView != null && userModel!=null) {
            setupDrawerContent(userModel);

            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            menuItem.setChecked(true);
                            switch (menuItem.getItemId()) {
                                case R.id.nav_sign_out:
                                    drawerLayout.closeDrawers();
                                    SharedPreferenceManager.getSharedInstance().clearAllPreferences();
                                    startLoginActivity();
                                    return true;
                                case R.id.nav_watchedList:
                                    drawerLayout.closeDrawers();
                                    SharedPreferenceManager.getSharedInstance().clearAllPreferences();
                                    startWatchActivity();
                                    return true;
                                case R.id.nav_toBeWatchedList:
                                    drawerLayout.closeDrawers();
                                    SharedPreferenceManager.getSharedInstance().clearAllPreferences();
                                    startToWatchActivity();
                                    return true;
                                case R.id.nav_favourite:
                                    drawerLayout.closeDrawers();
                                    SharedPreferenceManager.getSharedInstance().clearAllPreferences();
                                    startFavActivity();
                                    return true;
                                case R.id.nav_home:
                                    drawerLayout.closeDrawers();
                                    SharedPreferenceManager.getSharedInstance().clearAllPreferences();
                                    startHomeActivity();
                                    return true;
                                case R.id.nav_statistics:
                                    drawerLayout.closeDrawers();
                                    SharedPreferenceManager.getSharedInstance().clearAllPreferences();
                                    startStatisticsActivity();
                                    return true;
                                default:
                                    return true;
                            }
                        }
                    });
        }else{
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            menuItem.setChecked(true);
                            switch (menuItem.getItemId()) {
                                case R.id.nav_sign_in:
                                    drawerLayout.closeDrawers();
                                    SharedPreferenceManager.getSharedInstance().clearAllPreferences();
                                    startLoginActivity();
                                    return true;
                                default:
                                    return true;
                            }
                        }
                    });
        }


    }

    private void hideItem(boolean newUser)
    {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();

        if(newUser) {
            nav_Menu.findItem(R.id.nav_watchedList).setVisible(false);
            nav_Menu.findItem(R.id.nav_toBeWatchedList).setVisible(false);
            nav_Menu.findItem(R.id.nav_favourite).setVisible(false);
            nav_Menu.findItem(R.id.nav_sign_out).setVisible(false);
            nav_Menu.findItem(R.id.nav_home).setVisible(false);
            nav_Menu.findItem(R.id.nav_statistics).setVisible(false);
        }else {
            nav_Menu.findItem(R.id.nav_sign_in).setVisible(false);
        }
    }

    private void startWatchActivity() {
        Intent intent = new Intent(this, WatchedActivity.class);
        intent.putExtra("userModelClass", userModel);
        startActivity(intent);
        finishAffinity();
    }

    private void startToWatchActivity() {
        Intent intent = new Intent(this, ToBeWatchedActivity.class);
        intent.putExtra("userModelClass", userModel);
        startActivity(intent);
        finishAffinity();
    }

    private void startFavActivity() {
        Intent intent = new Intent(this, FavouriteActivity.class);
        intent.putExtra("userModelClass", userModel);
        startActivity(intent);
        finishAffinity();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("userModelClass", userModel);
        startActivity(intent);
        finishAffinity();
    }

    private void startStatisticsActivity() {
        Intent intent = new Intent(this, PieMainActivity.class);
        intent.putExtra("userModelClass", userModel);
        startActivity(intent);
        finishAffinity();
    }

    private void setupDrawerContent(UserModel userModel) {
        View headerView = navigationView.getHeaderView(0);

        simpleDraweeView = ButterKnife.findById(headerView, R.id.user_imageview);
        simpleDraweeView.setImageURI(Uri.parse(userModel.profilePic));

        nameTextView = ButterKnife.findById(headerView, R.id.name_textview);
        nameTextView.setText(userModel.userName);

        emailTextView = ButterKnife.findById(headerView, R.id.email_textview);
        emailTextView.setText(userModel.userEmail);
    }

    //Speech to Text
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    inputSearch.setText(text.get(0));
                }
                break;
            }

        }
    }
    //Speech to Text


}
