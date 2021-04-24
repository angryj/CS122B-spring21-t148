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
public class ShoppingCartServlet {
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
        String movieId = request.getParameter("id");
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            //query
            String query = "SELECT title FROM movies WHERE movies.id = ?;";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, movieId);
            ResultSet rs = statement.executeQuery();

            JsonObject jsonObject = new JsonObject();
            while (rs.next()) {
                String title = rs.getString("title");
                int price = 10 + Integer.parseInt(movieId.substring(movieId.length() - 2)) % 3;
                jsonObject.addProperty("movie_title", title);
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_price", price);
                jsonObject.addProperty("movie_quantity", 1);
            }

            HttpSession session = request.getSession();
            HashMap<String, JsonObject> cart = (HashMap<String, JsonObject>) session.getAttribute("cart");
            String action = request.getParameter("action");

            if (cart == null) {
                cart = new HashMap<>();
                session.setAttribute("cart", cart);
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
                case "delete":
                    cart.remove(movieId);
                    break;
                case "update":
                    int qty = Integer.parseInt(request.getParameter("qty"));
                    cart.get(movieId).addProperty("movie_quantity", qty);
                    break;
            }

            JsonArray jsonArray = new JsonArray();

            for (JsonObject movie : cart.values()) {
                jsonArray.add(movie);
            }

            out.write(jsonArray.toString());
            response.setStatus(200);
            rs.close();
            statement.close();
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
}
