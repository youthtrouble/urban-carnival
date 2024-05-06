package utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import models.Film;

public class RequestUtils {

    /**
     * Formats the response based on the specified content type and data.
     * @param format The content type (e.g., "application/json", "application/xml", "text/plain").
     * @param allFilms The list of films to format.
     * @return A string formatted according to the specified content type.
     * @throws JAXBException If an error occurs during XML formatting.
     */
    public static String formatResponse(String format, List<Film> allFilms) throws JAXBException {
        switch (format) {
            case "application/json":
                return marshallUtil.toJson(allFilms);  // Use Gson to convert Java objects to JSON format.
            case "application/xml":
            	return marshallUtil.toXml(allFilms, Film.class);  // Use JAXB to convert Java objects to XML format.
            case "text/plain":
                return allFilms.toString();  // Convert list to a string using the List's toString method.
            default:
                // Default to JSON if the format is not supported or specified
                return new Gson().toJson(allFilms);
        }
    }
    
    /**
     * Helper method to determine the correct content type based on the Accept header.
     * @param format The format specified in the Accept header.
     * @return The corresponding MIME type as a string.
     */
    public static String getContentType(String format) {
        switch (format) {
            case "application/json":
                return "application/json";
            case "application/xml":
                return "application/xml";
            case "text/plain":
                return "text/plain";
            default:
                return "application/json";  // Default to JSON
        }
    }
    
    /**
     * Configures CORS headers for the response.
     * Allows access from any origin and supports various HTTP methods and headers.
     */
    public static void setAccessControlHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    /**
     * Parses and validates the request body for film data based on content type.
     * @param request The HTTP request object.
     * @param contentType The content type of the request (e.g., "application/json", "application/xml").
     * @return A Film object if the parsing is successful; null otherwise.
     * @throws IOException If an error occurs during reading from the request.
     */
    public static Film parseRequestBody(HttpServletRequest request, String contentType) throws IOException {
        BufferedReader reader = request.getReader();
        try {
            if ("application/json".equals(contentType)) {
                return new Gson().fromJson(reader, Film.class);  // Parse JSON to a Film object.
            } else if ("application/xml".equals(contentType)) {
                JAXBContext jaxbContext = JAXBContext.newInstance(Film.class);
                return (Film) jaxbContext.createUnmarshaller().unmarshal(reader);  // Parse XML to a Film object.
            } else {
                return null;  // Return null if the content type is neither JSON nor XML.
            }
        } catch (Exception e) {
            return null;  // Return null if parsing fails.
        } finally {
            reader.close();  // Always close the BufferedReader.
        }
    }
}
