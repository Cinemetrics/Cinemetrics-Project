package edu.umkc.ase.cinemetrics;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.squareup.picasso.Picasso;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.umkc.ase.cinemetrics.model.MoviesModel;

/**
 * Created by vinuthna on 15-02-2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<View_Holder> {

    List<MoviesModel> list = Collections.emptyList();
    Context context;
    MongoClientURI uri = null;
    MongoClient client = null;
    MongoDatabase db = null;

    String userName = null;

    public MovieAdapter(List<MoviesModel> list, Context context, String userName) {
        this.list = list;
        this.context = context;
        this.userName = userName;
        java.security.Security.addProvider(new gnu.javax.crypto.jce.GnuSasl());
        uri  = new MongoClientURI("mongodb://moviedb:moviedb@ds155820.mlab.com:55820/movies_collection");
        client = new MongoClient(uri);
        db = client.getDatabase(uri.getDatabase());
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchlist_layout, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(View_Holder holder, int position) {
        holder.title.setText(list.get(position).getMovieName());
        holder.description.setText(list.get(position).getGenre());
        Picasso.with(holder.imageView.getContext()).load(list.get(position).getPosterId()).error(android.R.drawable.screen_background_dark_transparent).into(holder.imageView);

        final String movieName = list.get(position).getMovieName();
        final String moviePoster = list.get(position).getPosterId();
        final String genre = list.get(position).getGenre();
        final String language = list.get(position).getLanguage();
        final String releaseDate = list.get(position).getReleaseDate();


        holder.imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent movieredirect = new Intent(context, MovieDetailsActivity.class);
                movieredirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putString("movieName", movieName);
                bundle.putString("userName",userName);
                movieredirect.putExtras(bundle);
                context.startActivity(movieredirect);

            }
        });

        if(userName!=null) {

            final ToggleButton toggle = (ToggleButton) holder.watchtoggleButton;

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
                            .append("moviePoster", moviePoster).append("genre", genre).append("language", language).append("releaseDate", releaseDate);

                    if (toggle.isChecked()) {
                        moviesCollection.insertOne(movie);
                    } else {
                        moviesCollection.deleteOne(movie);
                        toggle.setChecked(false);
                    }
                }
            });

            //to watch
            final ToggleButton towatchtoggle = (ToggleButton) holder.towatchtoggleButton;

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
                            .append("moviePoster", moviePoster).append("genre", genre).append("language", language).append("releaseDate", releaseDate);

                    if (towatchtoggle.isChecked()) {
                        towatchmoviesCollection.insertOne(movie);
                    } else {
                        towatchmoviesCollection.deleteOne(movie);
                        towatchtoggle.setChecked(false);
                    }
                }
            });

            //favorite
            final ToggleButton favtoggle = (ToggleButton) holder.favtoggleButton;

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
                            .append("moviePoster", moviePoster).append("genre", genre).append("language", language).append("releaseDate", releaseDate);

                    if (favtoggle.isChecked()) {
                        favmoviesCollection.insertOne(movie);
                    } else {
                        favmoviesCollection.deleteOne(movie);
                        favtoggle.setChecked(false);
                    }
                }
            });

        }else{
           holder.watchtoggleButton.setVisibility(View.INVISIBLE);
            holder.towatchtoggleButton.setVisibility(View.INVISIBLE);
            holder.favtoggleButton.setVisibility(View.INVISIBLE);
        }

       // animate(holder);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView
    public void insert(int position, MoviesModel data) {
        list.add(position, data);
        notifyItemInserted(position);
    }
    // Remove a RecyclerView item containing the Data object
    public void remove(MoviesModel data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anticipate_overshoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    // Clean all elements of the recycler
    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<MoviesModel> list) {
        list.addAll(list);
        notifyDataSetChanged();
    }

}
