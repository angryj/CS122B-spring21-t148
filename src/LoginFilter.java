import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURLs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        String url = httpRequest.getRequestURI();
        url = url.substring(url.lastIndexOf("/") + 1, url.length());
        // Redirect to login page if the "user" attribute doesn't exist in session
        if(url.equals("_dashboard.html") && httpRequest.getSession().getAttribute("admin") != null)
        {
            chain.doFilter(request, response);
            return;
        }

        if(url.equals("_dashboard.html") && httpRequest.getSession().getAttribute("admin") == null)
        {
            httpResponse.sendRedirect("login-admin.html");
            return;
        }
        if (httpRequest.getSession().getAttribute("user") == null) {
            httpResponse.sendRedirect("login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        allowedURLs.add("style.css");
        return allowedURLs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURLs.add("login.html");
        allowedURLs.add("login.js");
        allowedURLs.add("api/login");

        allowedURLs.add("login-admin.html");
        allowedURLs.add("login-admin.js");
        allowedURLs.add("api/login-admin");

        allowedURLs.add("_dashboard.js");
        allowedURLs.add("api/metadata");




    }

    public void destroy() {
        // ignored.
    }

}
