package com.cinemetrics.cinematrics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    EditText movieTextField;
    public static String movieName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieTextField = (EditText) findViewById(R.id.txtMovieName);

    }

    //Navigate to movie details page
    public void FetchMovieDetails(View view)
    {
        movieName = movieTextField.getText().toString();
        Intent redirect = new Intent(MainActivity.this, MovieDetailsActivity.class);
        startActivity(redirect);
    }
}
