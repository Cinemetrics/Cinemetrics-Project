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

public class ToBeWatchedActivity extends AppCompatActivity {

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    SimpleDraweeView simpleDraweeView;

    TextView nameTextView;

    TextView emailTextView;

    RecyclerView recyclerView;
    private MovieAdapter adapter;
    //TextView toBeWatched;


    ArrayList<MoviesModel> finalmovieList = new ArrayList<MoviesModel>();

    MongoClientURI uri = null;
    MongoClient client = null;
    MongoDatabase db = null;

    UserModel userModel = null;
    String userName = null;


    public ToBeWatchedActivity(){


        java.security.Security.addProvider(new gnu.javax.crypto.jce.GnuSasl());
        uri  = new MongoClientURI("mongodb://moviedb:moviedb@ds155820.mlab.com:55820/movies_collection");
        client = new MongoClient(uri);
        db = client.getDatabase(uri.getDatabase());
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_be_watched_activity_search);
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

        userModel = (UserModel) getIntent().getParcelableExtra("userModelClass");

        if(userModel!=null){
            hideItem(false);
            userName = userModel.userEmail;
        }else{
            hideItem(true);
        }
        // if(userModel!=null)
            setDataOnNavigationView(userModel);

        MongoCollection<Document> moviesCollection = db.getCollection("towatch_movies");

        Document findQuery = new Document("username",userModel.userEmail);
       // Document findQuery = new Document("username",userName);

        MongoCursor<Document> cursor = moviesCollection.find(findQuery).iterator();


        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                MoviesModel movie = new MoviesModel(doc.get("movieTitle").toString(),doc.get("genre").toString(),doc.get("moviePoster").toString(),doc.get("releaseDate").toString(),doc.get("language").toString());
                finalmovieList.add(movie);

            }
        } finally {
            cursor.close();
        }

        navigatetoSearchField(finalmovieList);
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
        if (navigationView != null && userModel != null) {
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
        }else
            nav_Menu.findItem(R.id.nav_sign_in).setVisible(false);
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

    private void setupDrawerContent(UserModel userModel) {
        View headerView = navigationView.getHeaderView(0);

        simpleDraweeView = ButterKnife.findById(headerView, R.id.user_imageview);
        simpleDraweeView.setImageURI(Uri.parse(userModel.profilePic));

        nameTextView = ButterKnife.findById(headerView, R.id.name_textview);
        nameTextView.setText(userModel.userName);

        emailTextView = ButterKnife.findById(headerView, R.id.email_textview);
        emailTextView.setText(userModel.userEmail);
    }


}
