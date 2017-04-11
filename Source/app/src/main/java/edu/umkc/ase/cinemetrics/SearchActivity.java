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


    String API_URL = "https://api.themoviedb.org/3/discover/movie?";
    String API_KEY = "api_key=606d616b503278cd9d123c76c7e0e15f";
    String QUERY="&primary_release_year=2017&popularity>50&original_language=en";

    String SEARCH_API_URL = "https://api.themoviedb.org/3/search/movie?";
    String SEARCH_QUERY="&query=";
    String GENRE_QUERY="https://api.themoviedb.org/3/genre/movie/list?";
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

        UserModel userModel = getUserModelFromIntent();
        if(userModel!=null)
            setDataOnNavigationView(userModel);


        inputSearch = (EditText) findViewById(R.id.inputSearch);

        TextView movieCount = (TextView) findViewById(R.id.movieCount) ;

        movieCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptPieChart();
            }
        });

        long count = db.getCollection("movies").count();

        movieCount.setText("Movie Count: "+String.valueOf(count));

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


                    SearchActivity.MovieSearchAsyncTaskRunner runner = new SearchActivity.MovieSearchAsyncTaskRunner();

                    runner.execute("getURL",getURL);
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

//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View v, int position,
//                                    long arg3)
//            {
//                String movieName = arg0.getItemAtPosition(position).toString();
//
//                Intent redirect = new Intent(SearchActivity.this, MovieDetailsActivity.class);
//
//                Bundle bundle = new Bundle();
//                bundle.putString("movieName", movieName);
//                redirect.putExtras(bundle);
//                startActivity(redirect);
//
//            }
//        });
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
        adapter = new MovieAdapter(finalmovieList1, getApplication());
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
        if (navigationView != null) {
            setupDrawerContent(userModel);
        }

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
                                startWatchActivity();
                                return true;
                            case R.id.nav_favourite:
                                drawerLayout.closeDrawers();
                                SharedPreferenceManager.getSharedInstance().clearAllPreferences();
                                startWatchActivity();
                                return true;
                            default:
                                return true;
                        }
                    }
                });
    }

    private void startWatchActivity() {
        Intent intent = new Intent(this, WatchedActivity.class);
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
