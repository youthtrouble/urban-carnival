package controllers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import dao.FilmDao;
import models.Film;
import utils.RequestUtils;

/**
 * Servlet implementation for adding a new film.
 * Mapped to '/add-film' URL pattern.
 */
@WebServlet("/add-film")
public class AddFilmServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FilmDao dao = FilmDao.getInstance(); // Singleton instance of FilmDao

    /**
     * Handles POST request to add a new film. It expects data in various formats
     * and uses utility methods to parse the request body.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contentType = request.getContentType(); // Get the content type of the request
        PrintWriter out = response.getWriter(); // Get the PrintWriter object to write the response
        RequestUtils.setAccessControlHeaders(response); // Set CORS headers

        // Parse the request body into a Film object based on the content type
        Film film = RequestUtils.parseRequestBody(request, contentType);
        if (film == null) {
            // If parsing fails, send a 400 Bad Request response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("Invalid request data.");
            return;
        }

        try {
            // Validate that the film title is not null or empty
            if (film.getTitle() == null || film.getTitle().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("Title is required.");
                return;
            }

            // Insert the film into the database
            dao.insertFilm(film);
            response.setStatus(HttpServletResponse.SC_CREATED); // Set the status to 201 Created
            out.write(film.getTitle() + " has been added successfully."); // Write success message
        } catch (SQLException e) {
            // Handle SQL exceptions by throwing a ServletException
            throw new ServletException("SQL error occurred", e);
        } finally {
            out.close(); // Close the PrintWriter
        }
    }
}
