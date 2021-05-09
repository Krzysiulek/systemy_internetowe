package jerseyrest.students.student;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.morphia.annotations.*;
import jerseyrest.students.StudentsResource;
import jerseyrest.students.grade.Grade;
import jerseyrest.utils.LinkParser;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity("students")
@XmlRootElement(name = "student")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Indexes(@Index(options = @IndexOptions(name = "index"), fields = @Field("index")))
@XmlAccessorType(XmlAccessType.PROPERTY)
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
    public static final String FIRST_NAME = "firstName";

    @NonNull
    @Getter
    @Setter
    private String lastName;
    public static final String LAST_NAME = "lastName";

    @NonNull
    @Getter
    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    private Date birthday;
    public static final String BIRTHDAY = "birthday";

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
            @InjectLink(resource = StudentsResource.class,
                    method = "getStudent", rel = "self",
                    bindings = @Binding(name = "index", value = "${instance.index}")),
            @InjectLink(resource = StudentsResource.class, method = "getAllStudents", rel = "parent",
                    bindings = @Binding(name = "index", value = "${instance.index}")),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
//    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    @XmlJavaTypeAdapter(LinkParser.JaxbAdapter.class)
    @Getter
    @BsonIgnore
    private final List<Link> links = new ArrayList<>();

    // potrzebne, żeby pozbyć się wyjątku. Głupie, ale działa
    public String getBirthdayCompare() {
        return null;
    }

}
