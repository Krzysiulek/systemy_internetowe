package jerseyrest.courses;

import lombok.*;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "course")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Course {
    @NonNull
    @Getter
    @Setter
    private int id;

    @NonNull
    @Getter
    @Setter
    private String name;

    @NonNull
    @Getter
    @Setter
    private String lecturer;


    @InjectLinks({
            @InjectLink(resource = CourseResource.class, method = "getCourse", bindings = @Binding(name = "id", value = "${instance.id}"), rel = "self"),
            @InjectLink(resource = CourseResource.class, method = "getAllCourses", rel = "parent"),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    @Getter
    private final List<Link> links = new ArrayList<>();
}
