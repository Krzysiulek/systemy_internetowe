package jerseyrest.courses;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import jerseyrest.utils.ObjectIdJaxbAdapter;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

@Entity("courses")
@XmlRootElement(name = "course")
@NoArgsConstructor
@RequiredArgsConstructor
public class Course {
    @Id
    @Setter
    private ObjectId objectId;

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

//    @XmlTransient
public String getObjectId() {
    return objectId.toString();
}

    @InjectLinks({
            @InjectLink(resource = CourseResource.class, method = "getCourse", bindings = @Binding(name = "id", value = "${instance.id}"), rel = "self"),
            @InjectLink(resource = CourseResource.class, method = "getAllCourses", rel = "parent"),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    @Getter
//    @BsonIgnore
    private final List<Link> links = new ArrayList<>();
}
