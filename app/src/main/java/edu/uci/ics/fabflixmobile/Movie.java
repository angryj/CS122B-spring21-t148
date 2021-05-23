package edu.uci.ics.fabflixmobile;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Movie {
    private final String name;
    private final String director;
    private String movie_id;
    private final short year;
    private ArrayList<String> genres= new ArrayList<String>();
    private ArrayList<String> stars = new ArrayList<String>();

    public Movie(String name, short year) {
        this.name = name;
        this.year = year;
        director = "";
    }


    public Movie(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("movie_title");
        this.year = (short) jsonObject.getInt("movie_year");
        this.director = jsonObject.getString("movie_director");
        this.movie_id = jsonObject.getString("movie_id");
        String[] starsString = jsonObject.getString("movie_stars").split(",");
        String[] genresString = jsonObject.getString("movie_genres").split(",");
        int t = 0;
        for (String i : starsString) {
            stars.add(i);
        }
        for (String i : genresString) {
            genres.add(i);
        }
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public String getGenres() {
        String out = "";
        if (genres.size() == 1) {
            out = genres.get(0);
        }
        else if (genres.size() == 2) {
            out = genres.get(0) + ", " + genres.get(1);
        }
        else {
            out = genres.get(0) + ", " + genres.get(1) + ", " + genres.get(2);
        }
        return out;
    }

    public String getStars() {
        String out = "";
        if (stars.size() == 1) {
            out = stars.get(0);
        }
        else if (stars.size() == 2) {
            out = stars.get(0) + ", " + stars.get(1);
        }
        else {
            out = stars.get(0) + ", " + stars.get(1) + ", " + stars.get(2);
        }
        return out;
    }
}