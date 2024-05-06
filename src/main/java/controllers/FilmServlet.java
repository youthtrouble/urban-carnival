package controllers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.FilmDao;
import models.Film;
import utils.RequestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet to handle HTTP GET requests for film data, supporting JSON and XML formats.
 */
@WebServlet("/films")
public class FilmServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final FilmDao dao = FilmDao.getInstance(); // Singleton instance of FilmDao

    /**
     * Handles GET requests to retrieve film data in JSON or XML format.
     * Utilizes the 'Accept' header to determine the response format.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter writer = response.getWriter();
        RequestUtils.setAccessControlHeaders(response); // Set CORS headers for external access

        try {
            List<Film> allFilms = dao.getAllFilms(); // Retrieve all films from the database
            String format = request.getHeader("Accept"); // Determine the desired response format
            String responseText = RequestUtils.formatResponse(format, allFilms); // Format the response based on the request header
            
            response.setContentType(RequestUtils.getContentType(format)); // Set the content type of the response
            writer.write(responseText); // Write the formatted text to the response
        } catch (SQLException e) {
            // SQL error handling: set the response status to 500 and write an error message
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write("Database error: " + e.getMessage());
        } catch (Exception e) {
            // General error handling: handle other exceptions that might be thrown
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write("Internal server error: " + e.getMessage());
        } finally {
            writer.close(); // Ensure the PrintWriter is closed in the finally block
        }
    }

    /**
     * Handles HTTP OPTIONS requests, commonly used in CORS pre-flight checks.
     */
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestUtils.setAccessControlHeaders(resp); // Set necessary HTTP headers for CORS
        resp.setStatus(HttpServletResponse.SC_OK); // Respond with HTTP 200 OK for OPTIONS requests
    }
}
