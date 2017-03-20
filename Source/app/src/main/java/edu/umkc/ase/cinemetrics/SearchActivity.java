package edu.umkc.ase.cinemetrics;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

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
public class SearchActivity extends AppCompatActivity {

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    SimpleDraweeView simpleDraweeView;

    TextView nameTextView;

    TextView emailTextView;

    // List view
    private ListView lv;

    // Listview Adapter
    MovieAdapter adapter;

    // Search EditText
    EditText inputSearch;


    // ArrayList for Listview
    ArrayList<HashMap<String, String>> productList;

    //esha
    public static String movieName = "";

    String API_URL = "https://api.themoviedb.org/3/discover/movie?";
    String API_KEY = "api_key=606d616b503278cd9d123c76c7e0e15f";
    String QUERY="&primary_release_year=2017&popularity>50&original_language=en";

    String SEARCH_API_URL = "https://api.themoviedb.org/3/search/movie?";
    String SEARCH_QUERY="&query=";
    String GENRE_QUERY="https://api.themoviedb.org/3/genre/movie/list?";
    String sourceText;
    TextView outputTextView;
    ArrayList<MoviesModel> finalmovieList = new ArrayList<MoviesModel>();
    ArrayList<MoviesModel> finalmovieList1 = new ArrayList<MoviesModel>();
    public static Map<String,String> genreMap = new HashMap<String,String>();

    public SearchActivity(){
        //Get genre

        String getGenreUrl= GENRE_QUERY+API_KEY;

        OkHttpClient client0 = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(getGenreUrl)
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
            outputTextView.setText(ex.getMessage());

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        UserModel userModel = getUserModelFromIntent();
        if(userModel!=null)
            setDataOnNavigationView(userModel);

        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        TextView movieCount = (TextView) findViewById(R.id.movieCount) ;

        movieCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptPieChart();
            }
        });


        // Listview Data
        String getURL = API_URL+API_KEY+QUERY;//The API service URL
        final String response1 = "";
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(getURL)
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
                            final String[] genre = resultObj.get("genre_ids").toString().replace("[","").replace("]","").split(",");

                            String genreFields="";
                            for(String str:genre){
                                genreFields = genreMap.get(str)+",";
                            }
                            genreFields = genreFields.substring(0, genreFields.length()-1);

                            MoviesModel m = new MoviesModel(movieTitle,moviePoster,genreFields);

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
            outputTextView.setText(ex.getMessage());

        }


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

                finalmovieList1 = new ArrayList<MoviesModel>();

                if(arg0.length()>=4){

                    String getURL = SEARCH_API_URL+API_KEY+SEARCH_QUERY+arg0;//The API service URL
                    final String response1 = "";
                    OkHttpClient client = new OkHttpClient();
                    try {
                        Request request = new Request.Builder()
                                .url(getURL)
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
                                        //Log.d("okHttp", jsonResult.toString());
                                        final String moviePoster = resultObj.get("poster_path").toString();
                                        final String[] genre = resultObj.get("genre_ids").toString().replace("[","").replace("]","").split(",");

                                        String genreFields="";
                                        for(String str:genre){
                                            genreFields = genreMap.get(str)+",";
                                        }
                                        genreFields = genreFields.substring(0, genreFields.length()-1);

                                        MoviesModel m = new MoviesModel(movieTitle,moviePoster,genreFields);

                                        finalmovieList1.add(m);
                                    }


                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            navigatetoSearchField(finalmovieList1);
                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    } catch (Exception ex) {
                        outputTextView.setText(ex.getMessage());

                    }

                }

            }
        });

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

        // Adding items to listview
        adapter = new MovieAdapter(this,finalmovieList1);
        lv.setAdapter(adapter);


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
                            default:
                                return true;
                        }
                    }
                });
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
    public void FetchMovieDetails(View view)
    {
//        lv.setClickable(true);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//
//                Object o = lv.getItemAtPosition(position);
//            }
//        });
        movieName = "logan";
        Intent redirect = new Intent(SearchActivity.this, MovieDetailsActivity.class);
        startActivity(redirect);
    }

}
