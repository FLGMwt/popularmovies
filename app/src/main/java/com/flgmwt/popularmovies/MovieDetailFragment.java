package com.flgmwt.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by FLGMwt on 10/7/2016.
 */

public class MovieDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container);

        Intent intent = getActivity().getIntent();
        MovieSummary movie = intent.getParcelableExtra(getString(R.string.movie_extra_key));

        TextView titleView = (TextView) rootView.findViewById(R.id.movie_detail_title);
        titleView.setText(movie.title);

        TextView releaseYearView = (TextView) rootView.findViewById(R.id.movie_detail_release_year);
        String releaseYear = movie.releaseDate.split("-")[0];
        releaseYearView.setText(releaseYear);

        TextView ratingView = (TextView) rootView.findViewById(R.id.movie_detail_rating);
        ratingView.setText(movie.rating);

        TextView plotSynopsisView = (TextView) rootView.findViewById(R.id.movie_detail_plot_synopsis);
        plotSynopsisView.setText(movie.plotSynopsis);

        String moviePosterUrl = BuildConfig.MOVIE_POSTER_ROOT + BuildConfig.MOVIE_POSTER_SIZE_DETAIL + movie.posterUrl;
        ImageView poster = (ImageView)rootView.findViewById(R.id.movie_detail_poster);
        Picasso.with(getActivity()).load(moviePosterUrl).into(poster);
        poster.setContentDescription(getString(R.string.movie_poster_content_description) + movie.title);

        return rootView;
    }
}
