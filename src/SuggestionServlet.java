
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// server endpoint URL
@WebServlet("/api/suggestion")
public class SuggestionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /*
     * populate the Super hero hash map.
     * Key is hero ID. Value is hero name.
     */
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    public SuggestionServlet() {
        super();
    }

    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int counter = 1;
            Connection conn = dataSource.getConnection();
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");
            System.out.println("query is: " + query);

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
            //String q = "SELECT title from MOVIES WHERE MATCH(title) AGAINST (\'" + query + "\' IN BOOLEAN MODE)";
            String[] splitstring = query.split(" ");
            String newtitle = "";
            String temp = "";
            for(int i = 0;i<splitstring.length;i++)
            {
                temp = "+" + splitstring[i] + "* ";
                newtitle += temp;

            }
            //helper += "movies.title LIKE " + "\"%" + t + "%\"" + " and ";

            //String q = "SELECT title from MOVIES WHERE MATCH(title) AGAINST (\'" + newtitle + "\' IN BOOLEAN MODE) LIMIT 10";
            String q = "SELECT movies.title, movies.id, movies.year, movies.director, r.rating, GROUP_CONCAT(DISTINCT z.name) as genre, GROUP_CONCAT(DISTINCT s.name ORDER BY (SELECT COUNT(*) FROM stars_in_movies z WHERE z.starId = s.id GROUP BY s.id ) DESC, s.name) as name, GROUP_CONCAT(DISTINCT s.id ORDER BY (SELECT COUNT(*) FROM stars_in_movies z WHERE z.starId = s.id GROUP BY s.id ) DESC, s.name) as nameId"
                    + " FROM movies  INNER JOIN (SELECT ratings.movieId, ratings.rating FROM ratings) as r ON movies.id = r.movieId"
                    + " INNER JOIN( SELECT stars.id,stars.name, stars_in_movies.movieId FROM stars, stars_in_movies WHERE stars.id = stars_in_movies.starId) as s ON s.movieId = r.movieId"
                    + " INNER JOIN(SELECT genres.name,genres_in_movies.movieId FROM genres, genres_in_movies WHERE genres.id = genres_in_movies.genreId) as z on z.movieId = r.movieId"
                    + " WHERE MATCH(title) AGAINST  (\'" + newtitle + "\' IN BOOLEAN MODE) OR movies.title = \'" + query + "\'"
                    + " GROUP BY movies.id ORDER BY movies.title ASC LIMIT 10";
            PreparedStatement statement = conn.prepareStatement(q);
            ResultSet rs = statement.executeQuery(q);
            while(rs.next())
            {
                String title = rs.getString(1);
                System.out.println("title is: " + title);
                jsonArray.add(generateJsonObject(counter, title));
                counter +=1;

            }
            /*for (Integer id : superHeroMap.keySet()) {
                String heroName = superHeroMap.get(id);
                if (heroName.toLowerCase().contains(query.toLowerCase())) {
                    jsonArray.add(generateJsonObject(id, heroName));
                }
            }*/

            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(Integer heroID, String heroName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", heroName);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("heroID", heroID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}
