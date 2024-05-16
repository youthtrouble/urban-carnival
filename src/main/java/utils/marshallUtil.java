package utils;

import java.io.StringWriter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class marshallUtil {

    private static final Gson gson = new Gson();  // Create a single reusable Gson instance for performance.

    /**
     * Marshals a list of objects to an XML string using JAXB.
     * @param objects the list of objects to be marshaled
     * @param clazz the class of the objects in the list
     * @return XML string representation of the list
     * @throws JAXBException if an error occurs during marshaling
     */
    public static <T> String toXml(List<T> objects, Class<T> clazz) throws JAXBException {
        ObjectListWrapper<T> wrapper = new ObjectListWrapper<>(objects);  // Wrap the list of objects.

        JAXBContext context = JAXBContext.newInstance(ObjectListWrapper.class, clazz);  // Create a JAXBContext for the wrapper and the class.
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  // Format the XML output.

        StringWriter writer = new StringWriter();
        marshaller.marshal(wrapper, writer);  // Marshal the wrapper containing the list of objects into XML.

        return writer.toString();  // Convert StringWriter content to a string and return.
    }

    /**
     * Converts an object to a JSON string using Gson.
     * @param object the object to be converted to JSON
     * @return JSON string representation of the object
     */
    public static String toJson(Object object) {
        return gson.toJson(object);  // Convert the object to JSON using Gson.
    }

    /**
     * Converts a list of objects to a plain text string using a custom formatting function.
     * @param <T> the type parameter of the list
     * @param objects the list of objects to be converted
     * @param formatter a function that converts an object of type T to a String
     * @return a plain text representation of the list
     */
    public static <T> String toText(List<T> objects, Function<T, String> formatter) {
        return objects.stream()
                .map(formatter)  // Apply the formatter function to each object.
                .collect(Collectors.joining("\n"));  // Join all formatted strings with a newline character.
    }
}
