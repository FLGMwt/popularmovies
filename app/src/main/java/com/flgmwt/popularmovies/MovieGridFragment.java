/**
 * Copyright (C) 2016 Ryan Stelly- All Rights Reserved
 */
package com.flgmwt.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Fragment for Movie Grid
 * Handles fetching movies and sorting by rating or popularity */
public class MovieGridFragment extends Fragment {

    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    private MoviesAdapter mMoviesAdapter;
    private Map<String, List<MovieSummary>> mCachedMovies = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /** Inflates menu and sets the selected sort type as checked */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movie_list, menu);
        String sortPreference = getSortPreference();
        if (sortPreference.equals(getString(R.string.preferences_value_sort_order_popularity))) {
            menu.findItem(R.id.option_sort_by_popularity).setChecked(true);
        } else if (sortPreference.equals(getString(R.string.preferences_value_sort_order_rating))) {
            menu.findItem(R.id.option_sort_by_rating).setChecked(true);
        }

    }

    /** Fetches movies when a new sortType is selected and saves preference */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_sort_by_rating:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    String preferencesValue =
                            getString(R.string.preferences_value_sort_order_rating);
                    setSortOrderPreference(preferencesValue);
                    getMovies(preferencesValue);
                }
                return true;
            case R.id.option_sort_by_popularity:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    String preferencesValue =
                            getString(R.string.preferences_value_sort_order_popularity);
                    setSortOrderPreference(preferencesValue);
                    getMovies(preferencesValue);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSortOrderPreference(String preferencesValue) {
        SharedPreferences.Editor editor = getActivity().getPreferences(0).edit();
        editor.putString(
                getString(R.string.preferences_key_sort_order),
                preferencesValue
        );
        editor.apply();
    }

    /**
     * Checks internet connectivity to potentially show warning,
     * sets up gridView,
     * fires off initial movie load
     * */
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            TextView internetWarning = (TextView) rootView.findViewById(R.id.internet_warning);
            internetWarning.setVisibility(View.VISIBLE);
            setHasOptionsMenu(false);
        }

        mMoviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<MovieSummary>());
        GridView gridView = (GridView)rootView.findViewById(R.id.movie_grid_view);
        gridView.setAdapter(mMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieSummary movie = mMoviesAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra(getString(R.string.movie_extra_key), movie);
                startActivity(intent);
            }
        });

        String sortTypeKey = getSortPreference();
        getMovies(sortTypeKey);
        return rootView;
    }

    private String getSortPreference() {
        SharedPreferences preferences = getActivity().getPreferences(0);
        return preferences.getString(
                getString(R.string.preferences_key_sort_order),
                getString(R.string.preferences_value_sort_order_rating));
    }

    /** Refreshes adapter with new/different movies */
    private void refreshMovies(String sortTypeKey) {
        List<MovieSummary> movies = mCachedMovies.get(sortTypeKey);
        mMoviesAdapter.clear();
        mMoviesAdapter.addAll(movies);
    }

    /** Checks cache for movies for given sortType or queues request to fetch them */
    private void getMovies(final String sortTypeKey) {
        if (mCachedMovies.containsKey(sortTypeKey)) {
            refreshMovies(sortTypeKey);
            return;
        }

        String sortParameter = "";
        if (sortTypeKey.equals(getString(R.string.preferences_value_sort_order_rating))) {
            sortParameter = "top_rated";
        } else if (sortTypeKey.equals(getString(R.string.preferences_value_sort_order_popularity)))
        {
            sortParameter = "popular";
        }

        Uri requestUri = Uri
                .parse("https://api.themoviedb.org/3/movie")
                .buildUpon()
                .appendPath(sortParameter)
                .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                .build();
         RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(new StringRequest(requestUri.toString(),
            new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<MovieSummary> movies = parseMovieResponse(response);
                mCachedMovies.put(sortTypeKey, movies);
                refreshMovies(sortTypeKey);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "failed to fetch list with the following error: " + error.getMessage());
            }
        }));
    }

    /** Returns list of movie summaries pased from the json MovieDB response */
    private List<MovieSummary> parseMovieResponse(String json) {
        ArrayList<MovieSummary> movies = new ArrayList<>();
        try {
            JSONArray moviesJson = new JSONObject(json).getJSONArray("results");
            for (int i = 0; i < moviesJson.length(); i++) {
                JSONObject movieJson = moviesJson.getJSONObject(i);
                MovieSummary movie = new MovieSummary();
                movie.title = movieJson.getString("original_title");
                movie.posterUrl = movieJson.getString("poster_path");
                movie.plotSynopsis= movieJson.getString("overview");
                movie.rating = movieJson.getString("vote_average");
                movie.releaseDate = movieJson.getString("release_date");

                movies.add(movie);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "failed to parse json response of: " + json + " With message: " + e.getMessage());
        }
        return movies;
    }
}
