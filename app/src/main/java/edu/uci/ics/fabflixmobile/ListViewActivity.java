package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    public JSONArray jsonArray;
    public int page;
    public TextView pageNumber;
    private String baseURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        page = 1;
        pageNumber = findViewById(R.id.page);
        pageNumber.setText(page+"");

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


            /*
             final RequestQueue q = NetworkManager.sharedManager(this).queue;

            final StringRequest movieRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/Movie" + "?id=" + movie.getMovie_id(),
                response -> {
                    //message.setText(response);
                    Intent listPage = new Intent(ListViewActivity.this, ListViewActivity.this);
                    Bundle b = new Bundle();
                    b.putString("movies", response);
                    listPage.putExtras(b);
                    startActivity(listPage);
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }
            )

            {
                @Override
                protected Map<String, String> getParams() {
                    // POST request form data
                    final Map<String, String> params = new HashMap<>();

                    return params;
                }
            };
            q.add(movieRequest);
             */

        });
    }
}