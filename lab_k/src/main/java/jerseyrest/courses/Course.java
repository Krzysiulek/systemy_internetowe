package jerseyrest.courses;

import dev.morphia.annotations.*;
import jerseyrest.utils.LinkParser;
import lombok.*;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

@Entity("courses")
@XmlRootElement(name = "course")
@NoArgsConstructor
@RequiredArgsConstructor
@Indexes(@Index(options = @IndexOptions(name = "id"), fields = @Field("id")))
public class Course {
    @InjectLinks({
            @InjectLink(resource = CourseResource.class, method = "getCourse", bindings = @Binding(name = "id", value = "${instance.id}"), rel = "self"),
            @InjectLink(resource = CourseResource.class, method = "getAllCourses", rel = "parent"),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
//    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    @XmlJavaTypeAdapter(LinkParser.JaxbAdapter.class)
    @Getter
    private final List<Link> links = new ArrayList<>();

    @Id
    @Setter
    private ObjectId objectId;
    @NonNull
    @Getter
    @Setter
    private int id;
    public static final String ID = "id";
    @NonNull
    @Getter
    @Setter
    private String name;
    public static final String NAME = "name";

    @NonNull
    @Getter
    @Setter
    private String lecturer;
    public static final String LECTURER = "lecturer";

    @XmlTransient
    public String getObjectId() {
        return objectId.toString();
    }
}
