package jerseyrest.students.grade;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import jerseyrest.courses.Course;
import jerseyrest.utils.ObjectIdJaxbAdapter;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity("grades")
@XmlRootElement(name = "grade")
@NoArgsConstructor
@RequiredArgsConstructor
public class Grade {
    @Id
    @Setter
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private Object objectId;

    @NonNull
    @Getter
    @Setter
    private int id;

    @NonNull
    @Setter
    private int studentIndex;

    @NonNull
    @Getter
    @Setter
    private Double value;

    @NonNull
    @Getter
    @Setter
    private Date date;

    @NonNull
    @Getter
    @Setter
    private Course course;

    @XmlTransient
    public int getStudentIndex() {
        return studentIndex;
    }

    @XmlTransient
    public Object getObjectId() {
        return objectId;
    }

    @InjectLinks({
            @InjectLink(
                    resource = GradesResource.class,
                    method = "getGrade", bindings = {
                    @Binding(name = "index", value = "${instance.studentIndex}"),
                    @Binding(name = "gradeId", value = "${instance.id}")},
                    rel = "self"),
            @InjectLink(
                    resource = GradesResource.class,
                    method = "getAllGrades", bindings = {
                    @Binding(name = "index", value = "${instance.studentIndex}")},
                    rel = "parent"),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    @Getter
//    @BsonIgnore
    private final List<Link> links = new ArrayList<>();
}
