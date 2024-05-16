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
import utils.RequestUtils;

/**
 * Servlet implementation to handle deletion of a film.
 * Mapped to '/delete-film/*' URL pattern to include film ID as a path parameter.
 */
@WebServlet("/delete-film/*")
public class DeleteFilmServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FilmDao dao = FilmDao.getInstance(); // Use the singleton FilmDao instance.

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestUtils.setAccessControlHeaders(response); // Set necessary HTTP headers for CORS.

        PrintWriter out = response.getWriter();
        int filmId;

        // Extract the film ID from the path parameter.
        String pathInfo = request.getPathInfo(); // /{id}
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("Invalid film ID format.");
            out.close(); // Ensure PrintWriter is closed after writing response.
            return;
        }

        // Parse the ID from the path
        try {
            filmId = Integer.parseInt(pathInfo.substring(1)); // Extract the ID after '/'
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("Invalid film ID format.");
            out.close(); // Ensure PrintWriter is closed after writing response.
            return;
        }

        try {
            dao.deleteFilm(filmId);
            response.setStatus(HttpServletResponse.SC_OK);
            out.write("Film deleted successfully.");
        } catch (SQLException e) {
            if (e.getMessage().contains("No film found")) {
                // Set status to NOT FOUND if the deletion attempt failed because the film does not exist.
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("Film not found.");
            } else {
                // General SQL error handling.
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("SQL error occurred during film deletion: " + e.getMessage());
            }
        } finally {
            out.close(); // Always close the PrintWriter in a finally block to ensure resource release.
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
