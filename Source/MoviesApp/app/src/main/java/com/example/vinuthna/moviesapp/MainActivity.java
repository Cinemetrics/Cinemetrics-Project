package com.example.vinuthna.moviesapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // List view
    private ListView lv;

    // Listview Adapter
    MovieAdapter adapter;

    // Search EditText
    EditText inputSearch;


    // ArrayList for Listview
    ArrayList<HashMap<String, String>> productList;

    String API_URL = "https://api.themoviedb.org/3/discover/movie?";
    String API_KEY = "api_key=606d616b503278cd9d123c76c7e0e15f";
    String QUERY="&primary_release_year=2017&popularity>50&original_language=en";

    String SEARCH_API_URL = "https://api.themoviedb.org/3/search/movie?";
    String SEARCH_QUERY="&query=";
    String sourceText;
    TextView outputTextView;
    ArrayList<Movies> finalmovieList = new ArrayList<Movies>();
    ArrayList<Movies> finalmovieList1 = new ArrayList<Movies>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

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
                            Log.d("okHttp", jsonResult.toString());
                            final String moviePoster = resultObj.get("poster_path").toString();
                            Movies m = new Movies(movieTitle,moviePoster);

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

                finalmovieList1 = new ArrayList<Movies>();
                System.out.println(arg0);
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
                                        Log.d("okHttp", jsonResult.toString());
                                        final String moviePoster = resultObj.get("poster_path").toString();
                                        Movies m = new Movies(movieTitle,moviePoster);

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
    }

    private void navigatetoSearchField(ArrayList<Movies> finalmovieList1) {

        // Adding items to listview
        adapter = new MovieAdapter(this,finalmovieList1);
        lv.setAdapter(adapter);


    }


}
