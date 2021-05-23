package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListViewActivity extends Activity {
    public JSONArray jsonArray;
    public int page;
    public TextView pageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        page = 1;
        pageNumber = findViewById(R.id.page);
        pageNumber.setText(page+"");


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
            String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });
    }
}