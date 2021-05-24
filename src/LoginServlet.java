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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import org.jasypt.util.password.StrongPasswordEncryptor;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        String userAgent = request.getHeader("User-Agent");
        boolean isAndroid = userAgent.toLowerCase().contains("android");

        /*
            SELECT email, password
            FROM customers
            WHERE email = "a@email.com" AND password = "a2";
         */

        if (!isAndroid) {
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("message", e.getMessage());
                response.getWriter().write(jsonObject.toString());

                // set response status to 500 (Internal Server Error)
                response.setStatus(500);
                return;
            }
        }

        try (Connection conn = dataSource.getConnection()) {

            String query = "SELECT email, password, id FROM customers WHERE email = ?;";

            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, email);

            ResultSet rs = statement.executeQuery();

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("message", "incorrect password");
            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                boolean success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

                if (success) {
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                    request.getSession().setAttribute("user", new User());
                    request.getSession().setAttribute("id", rs.getInt("id"));
                    HashMap<String, JsonObject> cart = new HashMap<>();
                    request.getSession().setAttribute("cart", cart);
                }
                else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            else {
                responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
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
