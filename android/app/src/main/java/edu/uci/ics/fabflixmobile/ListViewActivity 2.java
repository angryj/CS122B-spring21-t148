package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static edu.uci.ics.fabflixmobile.EncodeURI.encodeURIComponent;

public class ListViewActivity extends Activity {
    private JSONArray jsonArray;
    private int page;
    private TextView pageNumber;
    private String baseURL;
    private String title;
    private Button next;
    private Button prev;
    private ArrayList<Movie> movies;
    private MovieListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        page = 1;
        pageNumber = findViewById(R.id.page);
        pageNumber.setText(page+"");
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);

        BaseURL t = new BaseURL();
        baseURL = t.baseURL;

        // TODO: this should be retrieved from the backend server

        Bundle bundle = this.getIntent().getExtras();
        title = bundle.getString("title");

        //movies.add(new Movie("The Terminal", (short) 2004));
        //movies.add(new Movie("The Final Season", (short) 2007));

        movies = getResponse(bundle.getString("movies"));

        adapter = new MovieListViewAdapter(movies, this);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        next.setOnClickListener(view -> next());
        prev.setOnClickListener(view -> prev());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            //String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            Intent singleMovie = new Intent(ListViewActivity.this, MovieViewActivity.class);

            Bundle b = new Bundle();
            b.putString("title", movie.getName());
            b.putString("director", movie.getDirector());
            b.putString("year", String.valueOf(movie.getYear()));
            b.putString("genres", movie.getGenresAll());
            b.putString("stars", movie.getStarsAll());

            singleMovie.putExtras(b);
            startActivity(singleMovie);

        });

    }

    public void next() {
        if (movies.size() != 20) {
            Toast.makeText(getApplicationContext(), "Last Page!", Toast.LENGTH_SHORT).show();
        }
        else {
            page++;
            final RequestQueue queue = NetworkManager.sharedManager(getParent()).queue;
            final StringRequest nextRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/Movie-List" + "?Title=" + title +"&show=20&pageNumber=" + page,
                response -> {
                    ArrayList<Movie> nextMovies = getResponse(response);
                    this.movies.clear();
                    for (Movie i: nextMovies) {
                        movies.add(i);
                    }

                    adapter.notifyDataSetChanged();
                    pageNumber.setText(page+"");
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }
        );
            queue.add(nextRequest);
        }
    }

    public void prev() {
        if (page == 1) {
            Toast.makeText(getApplicationContext(), "First Page!", Toast.LENGTH_SHORT).show();
        }
        else {
            page--;
            final RequestQueue queue = NetworkManager.sharedManager(getParent()).queue;
            final StringRequest prevRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/Movie-List" + "?Title=" + title +"&show=20&pageNumber=" + page,
                response -> {

                    ArrayList<Movie> prevMovies = getResponse(response);

                    this.movies.clear();
                    for (Movie i: prevMovies) {
                        movies.add(i);
                    }

                    adapter.notifyDataSetChanged();
                    pageNumber.setText(page+"");
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }
            );
            queue.add(prevRequest);
        }
    }

    public ArrayList<Movie> getResponse (String r) {
        ArrayList<Movie> m = new ArrayList<>();
        try {
            jsonArray = new JSONArray(r);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                m.add(new Movie(jsonObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return m;
    }
}