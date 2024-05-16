package utils;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "list")
public class ObjectListWrapper<T> {
    private List<T> objects;

    public ObjectListWrapper() {
    }

    public ObjectListWrapper(List<T> objects) {
        this.objects = objects;
    }

    @XmlElement(name = "object")
    public List<T> getObjects() {
        return objects;
    }

    public void setObjects(List<T> objects) {
        this.objects = objects;
    }
}
