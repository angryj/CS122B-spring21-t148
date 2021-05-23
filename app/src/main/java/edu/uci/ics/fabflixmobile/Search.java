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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
                baseURL + "/api/Movie-List" + "?Title=" + title.getText().toString() +"&",
                response -> {
                    message.setText(response);
                    Log.d("search.success", response);
                    Intent listPage = new Intent(Search.this, ListViewActivity.class);

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
