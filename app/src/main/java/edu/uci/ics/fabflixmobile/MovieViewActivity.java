package edu.uci.ics.fabflixmobile;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

public class MovieViewActivity extends Activity {

    private TextView title;
    private TextView director;
    private TextView year;
    private TextView genres;
    private TextView stars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie);

        title = findViewById(R.id.title);
        director = findViewById(R.id.director);
        year = findViewById(R.id.year);
        genres = findViewById(R.id.genres);
        stars = findViewById(R.id.stars);
        Bundle bundle = this.getIntent().getExtras();

        title.setText(bundle.getString("title"));
        director.setText(bundle.getString("director"));
        year.setText(bundle.getString("year"));
        genres.setText(bundle.getString("genres"));
        stars.setText(bundle.getString("stars"));

    }
}
