package com.flgmwt.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by FLGMwt on 10/6/2016.
 */

public class MoviesAdapter extends ArrayAdapter<MovieSummary> {

    private Context mContext;

    public MoviesAdapter(Context context, List<MovieSummary> movies) {
        super(context, 0, movies);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieSummary movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.movie_grid_item, parent, false);
        }
        TextView movieName = (TextView)convertView.findViewById(R.id.tmp_text);
        movieName.setText(movie.name);

//        ImageView poster = (ImageView)convertView.findViewById(R.id.tmp_img);
//        Picasso.with(mContext).load(movie.imageLink).into(poster);

        return convertView;
    }
}