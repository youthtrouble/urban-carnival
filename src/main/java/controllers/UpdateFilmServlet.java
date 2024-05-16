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
 * Servlet implementation to handle HTTP POST requests for updating film data.
 * Mapped to '/update-film' URL pattern.
 */
@WebServlet("/update-film")
public class UpdateFilmServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FilmDao dao = FilmDao.getInstance(); // Singleton instance of FilmDao

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            RequestUtils.setAccessControlHeaders(response); // Set CORS headers for cross-origin requests
            String contentType = request.getContentType(); // Get the content type of the incoming request
            
            // Parse the request body based on the content type
            Film film = RequestUtils.parseRequestBody(request, contentType);
            if (film == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("Invalid request data.");
                return;
            }

            // Validate that the film title is not null or empty
            if (film.getTitle() == null || film.getTitle().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("Title is required.");
                return;
            }

            // Perform the update operation
            dao.updateFilm(film);
            response.setStatus(HttpServletResponse.SC_OK);
            out.write(film.getTitle() + " has been updated successfully.");
        } catch (SQLException e) {
            // SQL error handling: throw a ServletException to indicate a server-side error
            throw new ServletException("SQL error occurred during film update: " + e.getMessage(), e);
        } finally {
            out.close(); // Ensure the PrintWriter is closed regardless of how the try block exits
        }
    }
    
    /**
     * Handles HTTP OPTIONS requests, commonly used in CORS pre-flight checks.
     */
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestUtils.setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
