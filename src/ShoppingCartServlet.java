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
import java.util.HashMap;


@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet{
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

            HttpSession session = request.getSession();
            HashMap<String, JsonObject> cart = (HashMap<String, JsonObject>) session.getAttribute("cart");
            if (cart == null) {
                cart = new HashMap<>();
                session.setAttribute("cart", cart);
            }

            String movieId = request.getParameter("id");
            String action = request.getParameter("action");
            if (movieId == null) {

            }
            else {
                //query
                String query = "SELECT title FROM movies WHERE movies.id = ?;";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, movieId);
                ResultSet rs = statement.executeQuery();

                JsonObject jsonObject = new JsonObject();
                while (rs.next()) {
                    String title = rs.getString("title");
                    int price = 10 + Integer.parseInt(movieId.substring(movieId.length() - 2)) % 10;
                    jsonObject.addProperty("movie_title", title);
                    jsonObject.addProperty("movie_id", movieId);
                    jsonObject.addProperty("movie_price", price);
                    jsonObject.addProperty("movie_quantity", 1);
                }

                switch(action) {
                    case "add":
                        if (cart.get(movieId) == null) {
                            cart.put(movieId, jsonObject);
                        }
                        else {
                            int qty = Integer.parseInt(cart.get(movieId).get("movie_quantity").toString());
                            cart.get(movieId).addProperty("movie_quantity", qty + 1);
                        }
                        break;
                    case "update":
                        int qty = Integer.parseInt(request.getParameter("qty"));
                        cart.get(movieId).addProperty("movie_quantity", qty);
                        break;
                    case "delete":
                        cart.remove(movieId);
                        break;
                    case "clear":
                        cart.clear();
                        break;
                }

                rs.close();
                statement.close();
            }

            JsonArray jsonArray = new JsonArray();

            for (JsonObject movie : cart.values()) {
                jsonArray.add(movie);
            }

            out.write(jsonArray.toString());
            response.setStatus(200);
        }
        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        }
        finally {
            out.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String card = request.getParameter("card");
        String expire = request.getParameter("expire");

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT id, expiration FROM creditcards WHERE id = ?";
            // ccId = 941, 11/01/2005
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, card);

            ResultSet rs = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();
            if (rs.next()) {
                if (expire.equals(rs.getString(2))) {
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
                else {
                    responseJsonObject.addProperty("status", "dateError");
                    responseJsonObject.addProperty("message", "invalid expiration date");
                }
            }
            else {
                responseJsonObject.addProperty("message", "Card " + card + " doesn't exist");
            }

            response.getWriter().write(responseJsonObject.toString());

        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", e.getMessage());
            response.getWriter().write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
    }

}
