import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/Movie-List")
public class MovieListServlet extends HttpServlet {

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            Statement statement = conn.createStatement();

            // prepare query
            String query = "SELECT movies.id, movies.title, movies.year, movies.director, r.rating, genres, stars, star_ids FROM movies INNER JOIN (SELECT ratings.movieId, ratings.rating FROM ratings ORDER BY rating DESC LIMIT 20) as r ON movies.id = r.movieId INNER JOIN (SELECT c.movieId, GROUP_CONCAT(name ORDER BY c.movieId) as stars, GROUP_CONCAT(id ORDER BY c.movieId) as star_ids FROM (SELECT r.movieId, stars.name, stars.id FROM stars_in_movies, (SELECT movieId FROM ratings ORDER BY rating DESC LIMIT 20) as r, stars WHERE stars_in_movies.movieId = r.movieId AND stars_in_movies.starId = stars.id) as c GROUP BY c.movieId) as s ON movies.id = s.movieId INNER JOIN ( SELECT c.movieId, GROUP_CONCAT(name ORDER BY c.movieId) as genres FROM (SELECT r.movieId, genres.name FROM genres, (SELECT movieId FROM ratings ORDER BY rating DESC LIMIT 20) as r, genres_in_movies WHERE genres_in_movies.movieId = r.movieId AND genres_in_movies.genreId = genres.id) as c GROUP BY c.movieId) as g ON movies.id = g.movieId;";
            // execute query
            ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();


            while (rs.next()) {
                String movie_id = rs.getString(1);
                String title = rs.getString(2);
                String year = rs.getString(3);
                String director = rs.getString(4);
                String rating = rs.getString(5);
                String genres = rs.getString(6);
                String stars = rs.getString(7);
                String star_ids = rs.getString(8);

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", title);
                jsonObject.addProperty("movie_year", year);
                jsonObject.addProperty("movie_director", director);
                jsonObject.addProperty("movie_rating", rating);
                jsonObject.addProperty("movie_genres", genres);
                jsonObject.addProperty("movie_stars", stars);
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("star_ids", star_ids);

                jsonArray.add(jsonObject);
            }

            rs.close();
            statement.close();
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}