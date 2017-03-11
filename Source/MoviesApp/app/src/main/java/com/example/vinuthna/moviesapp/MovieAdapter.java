package com.example.vinuthna.moviesapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by vinuthna on 15-02-2017.
 */

public class MovieAdapter extends ArrayAdapter<Movies> implements Filterable{

    public MovieAdapter(Context context, ArrayList<Movies> users) {

        super(context, 0, users);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Movies movies = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout, parent, false);
        }
        // Lookup view for data population
        TextView title_name = (TextView) convertView.findViewById(R.id.title_name);
        ImageView movieimageView = (ImageView) convertView.findViewById(R.id.movieimageView);
        // Populate the data into the template view using the data object
        title_name.setText(movies.getMovieName());
        Picasso.with(movieimageView.getContext()).load(movies.getPosterId()).error(android.R.drawable.sym_contact_card).placeholder(android.R.drawable.sym_contact_card).into(movieimageView);
        // Return the completed view to render on screen
        return convertView;
    }
}
