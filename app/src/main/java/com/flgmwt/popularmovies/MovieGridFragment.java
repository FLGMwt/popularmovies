package com.flgmwt.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


public class MovieGridFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movie_list, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container);

        MovieSummary movie1 = new MovieSummary();
        movie1.imageLink = "http://i.imgur.com/DvpvklR.png";
        movie1.name = "Blue";
        MovieSummary movie2 = new MovieSummary();
        movie2.imageLink = "http://i.imgur.com/DvpvklR.png";
        movie2.name = "Red";
        MovieSummary movie3 = new MovieSummary();
        movie3.imageLink = "http://i.imgur.com/DvpvklR.png";
        movie3.name = "Yellow";

        final List<MovieSummary>  movies = Arrays.asList(movie1, movie2, movie3);

        MoviesAdapter moviesAdapter = new MoviesAdapter(getActivity(), movies);

        GridView gridView = (GridView)rootView;
        gridView.setAdapter(moviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 MovieSummary movie = movies.get(position);

                Toast.makeText(getActivity(), movie.name, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
