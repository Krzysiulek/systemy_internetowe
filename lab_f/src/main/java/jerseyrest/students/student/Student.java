package jerseyrest.students.student;

import jerseyrest.students.StudentsResource;
import jerseyrest.students.grade.Grade;
import lombok.*;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "student")
@NoArgsConstructor
@RequiredArgsConstructor
public class Student {
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

    private List<Grade> grades = new ArrayList<>();

    void addGrade(Grade grade) {
        grades.add(grade);
    }

    void deleteGrade(Grade grade) {
        grades.remove(grade);
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

    @InjectLinks({
            @InjectLink(resource = StudentsResource.class, method = "getStudent", rel = "self"),
            @InjectLink(resource = StudentsResource.class, method = "getAllStudents", rel = "parent"),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    @Getter
    private List<Link> links = new ArrayList<>();
}
