package jerseyrest.students.grade;

import jerseyrest.courses.Course;
import lombok.*;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "grade")
@NoArgsConstructor
@RequiredArgsConstructor
public class Grade {
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
    private List<Link> links = new ArrayList<>();
}
