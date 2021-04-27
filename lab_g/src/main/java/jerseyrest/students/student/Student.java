package jerseyrest.students.student;

import dev.morphia.annotations.*;
import jerseyrest.students.StudentsResource;
import jerseyrest.students.grade.Grade;
import jerseyrest.utils.ObjectIdJaxbAdapter;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity("students")
@XmlRootElement(name = "student")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Indexes(@Index(options = @IndexOptions(name = "index"), fields = @Field("index")))
public class Student {
    @Id
    @Setter
    private ObjectId objectId;

    @NonNull
    @Getter
    @Setter
    private int index;

    @NonNull
    @Getter
    @Setter
    private String firstName;

    @NonNull
    @Getter
    @Setter
    private String lastName;

    @NonNull
    @Getter
    @Setter
    private Date birthday;

    @Setter
    private List<Grade> grades = new ArrayList<>();

    void addGrade(Grade grade) {
        grades.add(grade);
    }

    void deleteGrade(Grade grade) {
        grades.removeIf(g -> g.getId() == grade.getId());
    }

    void deleteGradeWithId(int courseId) {
        grades = grades.stream()
                       .filter(g -> g.getCourse()
                                     .getId() != courseId)
                       .collect(Collectors.toList());
    }

    /**
     * Ukrywa pole
     */
    @XmlTransient
    public List<Grade> getGrades() {
        return grades;
    }

    @XmlTransient
    public String getObjectId() {
        return objectId.toString();
    }

    @InjectLinks({
            @InjectLink(resource = StudentsResource.class, method = "getStudent", rel = "self"),
            @InjectLink(resource = StudentsResource.class, method = "getAllStudents", rel = "parent"),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    @Getter
    @BsonIgnore
    private final List<Link> links = new ArrayList<>();
}
