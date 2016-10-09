package com.flgmwt.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

        ImageView poster = (ImageView)convertView.findViewById(R.id.tmp_img);
        String moviePosterUrl = BuildConfig.MOVIE_POSTER_ROOT + BuildConfig.MOVIE_POSTER_SIZE_LIST + movie.posterUrl;
        Picasso.with(mContext).load(moviePosterUrl).into(poster);
        poster.setContentDescription(mContext.getString(R.string.movie_poster_content_description) + movie.title);

        return convertView;
    }
}
