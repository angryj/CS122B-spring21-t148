import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addmovie")
public class AddMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbmaster");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type
        HttpSession session = request.getSession();
        // Retrieve parameter id from url request.
        String title = request.getParameter("Title");
        System.out.println("title is: " + title);
        String director = request.getParameter("Director");
        String year = request.getParameter("Year");
        String star_name = request.getParameter("Star Name");
        String genre_name = request.getParameter("Genre Name");

       /* int yearint = 0;


        if(year.equals("") == false)
        {
            yearint = Integer.parseInt(year);
        }*/


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "CALL add_movie(?,?,?,?,?)";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,title);
            statement.setString(2,year);
            statement.setString(3,director);
            statement.setString(4,star_name);
            statement.setString(5,genre_name);
            statement.executeUpdate();
            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            String q2 = "SELECT * FROM movies, stars, genres, stars_in_movies, genres_in_movies" +
                    " WHERE movies.title = ? and stars.name = ? and genres.name = ? and " +
                    "stars_in_movies.movieId = movies.id and stars_in_movies.starId = stars.id " +
                    "and genres_in_movies.movieId = movies.id and genres_in_movies.genreId = genres.id";
            // Perform the query
            PreparedStatement statement2 = conn.prepareStatement(q2);
            statement2.setString(1,title);
            statement2.setString(2,star_name);
            statement2.setString(3,genre_name);

            ResultSet rs = statement2.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                JsonObject jsonObject = new JsonObject();
                String movieid = rs.getString(1);
                String starid = rs.getString(5);
                String genreid = rs.getString(8);
                System.out.println("movie id " + movieid);
                System.out.println("star id " + starid);
                System.out.println("genre id " + genreid);

                jsonObject.addProperty("movieid",movieid);
                jsonObject.addProperty("starid",starid);
                jsonObject.addProperty("genreid",genreid);
                // Declare our statement

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query

                // Create a JsonObject based on the data we retrieve from rs


                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // write JSON string to output
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

        // always remember to close db connection after usage. Here it's done by try-with-resources


    }

}