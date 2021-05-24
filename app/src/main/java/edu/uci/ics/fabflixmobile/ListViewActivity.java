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

public class ListViewActivity extends Activity {
    private JSONArray jsonArray;
    private int page;
    private TextView pageNumber;
    private String baseURL;
    private Button next;
    private Button prev;

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
        final ArrayList<Movie> movies = new ArrayList<>();
        //movies.add(new Movie("The Terminal", (short) 2004));
        //movies.add(new Movie("The Final Season", (short) 2007));


        try {
            jsonArray = new JSONArray(bundle.getString("movies"));
            int len = jsonArray.length();
            for (int i = (page-1)*20; i < Math.min(page*20, jsonArray.length() - (page - 1) * 20); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                movies.add(new Movie(jsonObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

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

    }

    public void prev() {

    }
}