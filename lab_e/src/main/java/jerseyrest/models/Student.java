package jerseyrest.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "student")
@Data
@NoArgsConstructor
public class Student {
    private int index;
    private String firstName;
    private String lastName;
    private Date birthday;


    private List<Grade> grades = new ArrayList<>();

    public Student(int index, String firstName, String lastName, Date birthday) {
        this.index = index;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public void deleteGrade(Grade grade) {
        grades.remove(grade);
    }

    // to hide field
    @XmlTransient
    public List<Grade> getGrades() {
        return grades;
    }
}
