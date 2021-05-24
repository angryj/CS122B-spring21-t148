package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static edu.uci.ics.fabflixmobile.EncodeURI.encodeURIComponent;

public class Search extends Activity {
    private EditText title;
    private TextView message;
    private Button submit;
    private String baseURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.search);
        submit = findViewById(R.id.submit);
        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        message.setText("");
        BaseURL t = new BaseURL();
        baseURL = t.baseURL;

        //assign a listener to call a function to handle the user request when clicking a button
        submit.setOnClickListener(view -> search());
    }

    public void search() {
        message.setText("Trying to search");
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final StringRequest searchRequest = new StringRequest(
            Request.Method.GET,
            baseURL + "/api/Movie-List" + "?Title=" + encodeURIComponent(title.getText().toString()) +"&show=20",
            response -> {
                //message.setText(response);
                Log.d("search.success", response);
                Intent listPage = new Intent(Search.this, ListViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("movies", response);
                bundle.putString("title", encodeURIComponent(title.getText().toString()));
                listPage.putExtras(bundle);
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
                params.put("Title", title.getText().toString());
                return params;
            }
        };
        queue.add(searchRequest);
    }

}
