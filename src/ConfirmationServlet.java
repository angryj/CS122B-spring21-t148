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


@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet{
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbmaster");
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
            JsonArray jsonArray = new JsonArray();

            int customerId = (int) session.getAttribute("id");

            for (JsonObject movie : cart.values()) {
                String movieId = movie.get("movie_id").getAsString();
                int qty = movie.get("movie_quantity").getAsInt();
                long ms = System.currentTimeMillis();
                java.sql.Date saleDate = new java.sql.Date(ms);

                String insert = "INSERT INTO sales VALUES(DEFAULT, ?, ?, ?);";
                PreparedStatement s2 = conn.prepareStatement(insert);
                s2.setInt(1, customerId);
                s2.setString(2, movieId);
                s2.setDate(3, saleDate);
                s2.executeUpdate();

                String q2 = "SELECT id FROM sales WHERE id=(SELECT MAX(id) FROM sales);";
                PreparedStatement s3 = conn.prepareStatement(q2);
                ResultSet rs2 = s3.executeQuery();
                int saleId = 0;
                while (rs2.next()) {
                    saleId = rs2.getInt(1);
                }

                String insert2 = "INSERT INTO cart VALUES(?, ?);";
                PreparedStatement s4 = conn.prepareStatement(insert2);
                s4.setInt(1, saleId);
                s4.setInt(2, qty);
                s4.executeUpdate();

                jsonArray.add(movie);
            }

            out.write(jsonArray.toString());
            cart.clear();
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
}
