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
@WebServlet(name = "CheckMovieServlet", urlPatterns = "/api/checkmovie")
public class CheckMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
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
        String director = request.getParameter("Director");
        String year = request.getParameter("Year");
        int yearint = 0;
        boolean movie_found = false;
        if(year.equals("") == false)
        {
            yearint = Integer.parseInt(year);
        }


        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT * FROM movies WHERE movies.title = ? AND movies.year = ? and movies.director = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,title);
            statement.setString(2,year);
            statement.setString(3,director);

            System.out.println(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                System.out.println("AAAAAA");
                movie_found = true;

                // Declare our statement

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query

                // Create a JsonObject based on the data we retrieve from rs

                //jsonObject.addProperty("id",newstring);

            }
            rs.close();
            statement.close();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movie_found",movie_found);
            jsonArray.add(jsonObject);


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