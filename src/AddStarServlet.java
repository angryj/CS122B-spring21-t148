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
@WebServlet(name = "AddStarServlet", urlPatterns = "/api/addstar")
public class AddStarServlet extends HttpServlet {
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
        String name = request.getParameter("Name");
        String year = request.getParameter("Year");
        int yearint = 0;


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
            String query = "SELECT max(id) FROM STARS";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {


                String max_id =  rs.getString("max(id)");
                int n = Integer.parseInt (max_id.replaceFirst("^.*\\D",""));
                int newid = n+1;
                String newstring = "nm" + Integer.toString(newid);

                if(year.equals("") == false) {
                    String insert = "INSERT INTO stars VALUES(?, ?, ?);";
                    PreparedStatement s2 = conn.prepareStatement(insert);
                    s2.setString(1, newstring);
                    s2.setString(2, name);
                    s2.setInt(3, yearint);
                    s2.executeUpdate();

                }
                else{
                    String insert = "INSERT INTO stars VALUES(?, ?, NULL);";
                    PreparedStatement s2 = conn.prepareStatement(insert);
                    s2.setString(1, newstring);
                    s2.setString(2, name);
                    s2.executeUpdate();
                }
                // Declare our statement

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("table",max_id);

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