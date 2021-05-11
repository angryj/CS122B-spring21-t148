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
        HttpSession session = request.getSession();
        String queryString = request.getQueryString();
        session.setAttribute("params",queryString);

        try (Connection conn = dataSource.getConnection()) {
            String t = request.getParameter("Title");
            String y = request.getParameter("Year");
            String d = request.getParameter("Director");
            String n = request.getParameter("Name");
            String l = request.getParameter("letter");
            String g = request.getParameter("genre");
            String s = request.getParameter("sort");
            String show = request.getParameter("show");
            String page = request.getParameter("pageNumber");
            String sortBy = "";
            String pageNumber = "";
            String offsetAsString = "0";



            //if there is no show parameter, use a default of 25
            if(show == null)
            {
                show = "25";
            }

            //if there is no page number parameter, offset is 0
            if(page == null)
            {
                offsetAsString = "0";
            }
            else{
                int temp = Integer.parseInt(show);
                int temp2 = Integer.parseInt(page);
                int offset = temp*temp2 - temp;
                offsetAsString = Integer.toString(offset);
            }

            //turns the sort parameter into a query
            if(s == null)
            {
                sortBy = "movies.title ASC, r.rating DESC";
            }

            else if(s.equals("titleASCratingDESC"))
            {
                sortBy = "movies.title ASC, r.rating DESC";
            }

            else if(s.equals("titleASCratingASC"))
            {
                sortBy = "movies.title ASC, r.rating ASC";
            }

            else if(s.equals("titleDESCratingDESC"))
            {
                sortBy = "movies.title DESC, r.rating DESC";
            }

            else if(s.equals("titleDESCratingASC"))
            {
                sortBy = "movies.title DESC, r.rating ASC";
            }

            else if(s.equals("ratingASCtitleDESC"))
            {
                sortBy = "r.rating ASC, movies.title DESC";
            }

            else if(s.equals("ratingASCtitleASC"))
            {
                sortBy = "r.rating ASC, movies.title ASC";
            }

            else if(s.equals("ratingDESCtitleDESC"))
            {
                sortBy = "r.rating DESC, movies.title DESC";
            }

            else if(s.equals("ratingDESCtitleASC"))
            {
                sortBy = "r.rating DESC, movies.title ASC";
            }

            //helper is used for the WHERE part of the query
            String helper = "";

            //helper2 is used for the HAVING part of the query
            String helper2 = "";

            //turn the URL parameters from a user search into a query
            if (g!=null)
            {
                helper2 += "HAVING genre LIKE " + "\"%" + g + "%\"";
            }
            if(l!=null) {
                if (l.equals("*") == false)
                {
                    helper += "movies.title LIKE " + "\"" + l + "%\"" + " and";
                }
                else{
                    helper +="movies.title REGEXP " +  "\"^[^0-9A-Za-z]\"" + " and";
                }
            }
            if(t!=null)
            {
                helper += "movies.title LIKE " + "\"%" + t + "%\"" + " and ";
            }
            if(y!= null)
            {
                helper += "movies.year = " + y + " and ";
            }
            if(d!= null)
            {
                helper += "movies.director LIKE " + "\"%" + d + "%\"" + " and ";
            }

            if(n!=null)
            {
                helper2 += "HAVING name LIKE " + "\"%" + n + "%\"";
            }

            //strip off the "and" at the end of the helper
            if(helper != "") {
                helper = helper.substring(0, helper.length() - 4);
            }

            // prepare query
            //String query = "SELECT movies.id, movies.title, movies.year, movies.director, r.rating, genres, stars, star_ids FROM movies INNER JOIN (SELECT ratings.movieId, ratings.rating FROM ratings ORDER BY rating DESC LIMIT 20) as r ON movies.id = r.movieId INNER JOIN (SELECT c.movieId, GROUP_CONCAT(name ORDER BY c.movieId) as stars, GROUP_CONCAT(id ORDER BY c.movieId) as star_ids FROM (SELECT r.movieId, stars.name, stars.id FROM stars_in_movies, (SELECT movieId FROM ratings ORDER BY rating DESC LIMIT 20) as r, stars WHERE stars_in_movies.movieId = r.movieId AND stars_in_movies.starId = stars.id) as c GROUP BY c.movieId) as s ON movies.id = s.movieId INNER JOIN ( SELECT c.movieId, GROUP_CONCAT(name ORDER BY c.movieId) as genres FROM (SELECT r.movieId, genres.name FROM genres, (SELECT movieId FROM ratings ORDER BY rating DESC LIMIT 20) as r, genres_in_movies WHERE genres_in_movies.movieId = r.movieId AND genres_in_movies.genreId = genres.id) as c GROUP BY c.movieId) as g ON movies.id = g.movieId;";
            // execute query
            String query = "";

            //query for everything that isnt genre browsing and name browsing
            if(g == null && n == null ) {
                 query = "SELECT movies.id, movies.title, movies.year, movies.director, r.rating, GROUP_CONCAT(DISTINCT z.name) as genre, GROUP_CONCAT(DISTINCT s.name ORDER BY (SELECT COUNT(*) FROM stars_in_movies z WHERE z.starId = s.id GROUP BY s.id ) DESC, s.name) as name, GROUP_CONCAT(DISTINCT s.id ORDER BY (SELECT COUNT(*) FROM stars_in_movies z WHERE z.starId = s.id GROUP BY s.id ) DESC, s.name) as nameId"
                        + " FROM movies  INNER JOIN (SELECT ratings.movieId, ratings.rating FROM ratings) as r ON movies.id = r.movieId"
                        + " INNER JOIN( SELECT stars.id,stars.name, stars_in_movies.movieId FROM stars, stars_in_movies WHERE stars.id = stars_in_movies.starId) as s ON s.movieId = r.movieId"
                        + " INNER JOIN(SELECT genres.name,genres_in_movies.movieId FROM genres, genres_in_movies WHERE genres.id = genres_in_movies.genreId) as z on z.movieId = r.movieId";


                if (helper != "") {
                    query += " WHERE " + helper;
                }
                //if (helper2 != "") {
               //     query += helper2;
               // }
                query += " GROUP BY movies.id ";
                query += " ORDER BY " + sortBy;
                query += " LIMIT " + show + " OFFSET " + offsetAsString;
            }

            //query for name browsing
            else if(g== null && n!=null)
            {
                query = "SELECT movies.id, movies.title, movies.year, movies.director, r.rating, "
                        + " GROUP_CONCAT(DISTINCT g.name) as genre, "
                        + " GROUP_CONCAT(DISTINCT ss.name ORDER BY (SELECT COUNT(*) FROM stars_in_movies z WHERE z.starId = ss.id GROUP BY ss.id ) DESC, ss.name) as name,"
                        + " GROUP_CONCAT(DISTINCT ss.id ORDER BY (SELECT COUNT(*) FROM stars_in_movies z WHERE z.starId = ss.id GROUP BY ss.id ) DESC, ss.name) as nameId "
                        + " FROM movies "
                        + " INNER JOIN(SELECT stars.id,stars.name, stars_in_movies.movieId FROM stars, stars_in_movies WHERE stars.id = stars_in_movies.starId and stars.name LIKE " +  "\"%" + n + "%\"" +  ") as s ON s.movieId = movies.id"
                        + " INNER JOIN(SELECT stars.id, stars.name, stars_in_movies.movieId FROM stars, stars_in_movies WHERE stars_in_movies.starId = stars.id ) as ss on ss.movieId = s.movieId"
                        + " INNER JOIN (SELECT ratings.movieId, ratings.rating FROM ratings) as r ON s.movieId = r.movieId  "
                        + " INNER JOIN(SELECT genres.name,genres_in_movies.movieId FROM genres, genres_in_movies WHERE genres.id = genres_in_movies.genreId) as g on g.movieId = r.movieId ";
                        if (helper != "") {
                            query += " WHERE " + helper;
                        }
                        query += " GROUP BY movies.id ";
                        query += " ORDER BY " + sortBy;
                        query += " LIMIT " + show + " OFFSET " + offsetAsString;


                System.out.println(query);
            }
            //genre browsing query
            else{

                query = "SELECT movies.id, movies.title, movies.year, movies.director, r.rating, GROUP_CONCAT(DISTINCT z.name) as genre, GROUP_CONCAT(DISTINCT s.name ORDER BY (SELECT COUNT(*) FROM stars_in_movies z WHERE z.starId = s.id GROUP BY s.id ) DESC, s.name) as name, GROUP_CONCAT(DISTINCT s.id ORDER BY (SELECT COUNT(*) FROM stars_in_movies z WHERE z.starId = s.id GROUP BY s.id ) DESC, s.name) as nameId"
            + " FROM movies INNER JOIN(SELECT genres.name,genres_in_movies.movieId, movies.title, r.rating FROM genres, genres_in_movies, movies,ratings r WHERE r.movieId = movies.id and movies.id = genres_in_movies.movieId and genres.id = genres_in_movies.genreId and genres.name = " + "\"" + g + "\"" +   " ORDER BY " + sortBy + " LIMIT " + show + " OFFSET " + offsetAsString + ") as r on r.movieId = movies.Id"
            + " INNER JOIN(SELECT genres.name,genres_in_movies.movieId FROM genres, genres_in_movies WHERE genres.id = genres_in_movies.genreId) as z on r.movieId = z.movieId"
                        + " INNER JOIN(SELECT stars.id,stars.name, stars_in_movies.movieId FROM stars, stars_in_movies WHERE stars.id = stars_in_movies.starId) as s ON s.movieId = z.movieId "
                        + " GROUP BY movies.id"
                        +" ORDER BY " + sortBy;

            }

            PreparedStatement statement = conn.prepareStatement(query);
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