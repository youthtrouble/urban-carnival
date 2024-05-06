package models;

import java.sql.Timestamp;

/**
 * Represents a film entity with properties to describe a film's details.
 * This class is annotated to be compatible with JAXB for XML serialisation.
 */
public class Film {
    private int id;
    private String title;
    private int year;
    private String director;
    private String stars;
    private String review;

    /**
     * Default constructor for JAXB serialization and deserialization.
     */
    public Film() {
        // Intentionally empty for frameworks and libraries that require an empty constructor.
    }

    /**
     * Constructs a Film with specified details, useful for creating a new Film record.
     */
    public Film(String title, int year, String director, String stars, String review) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.stars = stars;
        this.review = review;
    }

    /**
     * Constructs a Film with full details, including database-specific ID fields.
     */
    public Film(int id, String title, int year, String director, String stars, String review) {
        this(title, year, director, stars, review); // Reuse constructor
        this.id = id;
    }

    // Getters and Setters for all properties.
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    /**
     * Provides a string representation of a film, showing basic film details.
     */
    @Override
    public String toString() {
        return "Film [id=" + id + ", title=" + title + ", year=" + year + ", director=" + director
                + ", stars=" + stars + ", review=" + review + "]";
    }
}
