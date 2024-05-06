package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import models.Film;

/**
 * Singleton Data Access Object for managing Film entities in the database.
 */
public class FilmDao {

    private static final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/cloud";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "my-secret-pw";

    // Private constructor to prevent instantiation outside of this class.
    private FilmDao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensures that the MySQL driver is loaded.
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver not found", e);
        }
    }

    /**
     * Holds the singleton instance of FilmDao in a thread-safe manner.
     */
    private static class SingletonHolder {
        private static final FilmDao INSTANCE = new FilmDao();
    }

    /**
     * Provides the global point of access to the FilmDao instance.
     * @return the singleton instance of FilmDao.
     */
    public static FilmDao getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Creates and returns a connection to the database.
     * @return a new Connection object.
     * @throws SQLException If a connection error occurs.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    /**
     * Extracts a Film object from the ResultSet.
     * @param rs The ResultSet object containing the film data.
     * @return a Film object.
     * @throws SQLException If an error occurs reading from the ResultSet.
     */
    private Film extractFilmFromResultSet(ResultSet rs) throws SQLException {
        return new Film(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getInt("year"),
            rs.getString("director"),
            rs.getString("stars"),
            rs.getString("review")
        );
    }

    /**
     * Retrieves a single film from the database by its ID.
     * @param id The ID of the film to retrieve.
     * @return A Film object representing the found film.
     * @throws SQLException If a database access error occurs or no film is found.
     */
    public Film getFilmById(int id) throws SQLException {
        String sql = "SELECT * FROM films WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractFilmFromResultSet(rs);
                } else {
                    throw new SQLException("No film found with ID: " + id);
                }
            }
        }
    }

    /**
     * Retrieves all films from the database.
     * @return An ArrayList of Film objects.
     * @throws SQLException If a database access error occurs.
     */
    public ArrayList<Film> getAllFilms() throws SQLException {
        ArrayList<Film> allFilms = new ArrayList<>();
        String sql = "SELECT * FROM films;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                allFilms.add(extractFilmFromResultSet(rs));
            }
        }
        return allFilms;
    }

    /**
     * Inserts a new film into the database.
     * @param film The Film object to insert.
     * @throws SQLException If a database access error occurs or no ID is obtained.
     */
    public void insertFilm(Film film) throws SQLException {
        String sql = "INSERT INTO films (title, year, director, stars, review) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, film.getTitle());
            pstmt.setInt(2, film.getYear());
            pstmt.setString(3, film.getDirector());
            pstmt.setString(4, film.getStars());
            pstmt.setString(5, film.getReview());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating film failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    film.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating film failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Updates an existing film in the database.
     * @param film The Film object to update.
     * @throws SQLException If a database access error occurs or the update fails.
     */
    public void updateFilm(Film film) throws SQLException {
        String sql = "UPDATE films SET title = ?, year = ?, director = ?, stars = ?, review = ? WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, film.getTitle());
            pstmt.setInt(2, film.getYear());
            pstmt.setString(3, film.getDirector());
            pstmt.setString(4, film.getStars());
            pstmt.setString(5, film.getReview());
            pstmt.setInt(6, film.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating film failed, no rows affected.");
            }
        }
    }

    /**
     * Deletes a film from the database based on its ID.
     * @param id The ID of the film to delete.
     * @throws SQLException If a database access error occurs or the deletion fails.
     */
    public void deleteFilm(int id) throws SQLException {
        String sql = "DELETE FROM films WHERE id = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting film failed, no rows affected.");
            }
        }
    }

    /**
     * Searches for films based on a search string that matches title, director, or stars.
     * @param searchStr The string to search for.
     * @return An ArrayList of Film objects that match the search criteria.
     * @throws SQLException If a database access error occurs.
     */
    public ArrayList<Film> searchFilm(String searchStr) throws SQLException {
        ArrayList<Film> searchResults = new ArrayList<>();
        String sql = "SELECT * FROM films WHERE LOWER(title) LIKE LOWER(?) OR LOWER(director) LIKE LOWER(?) OR LOWER(stars) LIKE LOWER(?);";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchWithWildcards = "%" + searchStr + "%";
            pstmt.setString(1, searchWithWildcards);
            pstmt.setString(2, searchWithWildcards);
            pstmt.setString(3, searchWithWildcards);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    searchResults.add(extractFilmFromResultSet(rs));
                }
            }
        }
        return searchResults;
    }
}
