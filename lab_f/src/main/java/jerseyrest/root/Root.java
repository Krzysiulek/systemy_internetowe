package jerseyrest.root;

import jerseyrest.courses.CourseResource;
import jerseyrest.students.StudentsResource;
import lombok.Getter;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

public class Root {

    @InjectLinks({
            @InjectLink(resource = StudentsResource.class, method = "getAllStudents", rel = "students"),
            @InjectLink(resource = CourseResource.class, method = "getAllCourses", rel = "courses"),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    @Getter
    private List<Link> links = new ArrayList<>();
}
