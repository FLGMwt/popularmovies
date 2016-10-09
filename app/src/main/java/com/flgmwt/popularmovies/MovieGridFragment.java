package com.flgmwt.popularmovies;

import android.app.Fragment;
import android.content.Intent;
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


public class MovieGridFragment extends Fragment {

    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();

    private MoviesAdapter moviesAdapter;
    private Map<SortType, List<MovieSummary>> cachedMovies = new HashMap<>();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_grid, container);

        moviesAdapter = new MoviesAdapter(getActivity(), new ArrayList<MovieSummary>());
        GridView gridView = (GridView)rootView;
        gridView.setAdapter(moviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieSummary movie = moviesAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                startActivity(intent);
            }
        });

        getMovies(SortType.Rating);
        return rootView;
    }

    private void refreshMovies() {
        List<MovieSummary> movies = cachedMovies.get(SortType.Rating);
        moviesAdapter.clear();
        moviesAdapter.addAll(movies);
    }

    private void getMovies(final SortType sortType) {
        String sortParamemter = "";
        switch (sortType) {
            case Rating:
                sortParamemter = "top_rated";
                break;
            case Popularity:
                sortParamemter = "popular";
                break;
        }
        Uri requestUri = Uri
                .parse("https://api.themoviedb.org/3/movie")
                .buildUpon()
                .appendPath(sortParamemter)
                .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY)
                .build();
        Log.e("uri", requestUri.toString());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(new StringRequest(requestUri.toString(),
            new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<MovieSummary> movies = parseMovieResponse(response);
                cachedMovies.put(sortType, movies);
                refreshMovies();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "failed to fetch list with the following error: " + error.getMessage());
            }
        }));
    }

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

    private enum SortType {
        Rating,
        Popularity
    }
}
