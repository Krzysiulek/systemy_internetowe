package jerseyrest.students.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class StudentResponse {
    private int index;
    private String firstName;
    private String lastName;
    private Date birthday;

    public static StudentResponse of(Student student) {
        return new StudentResponse(student.getIndex(), student.getFirstName(), student.getLastName(),
                                   student.getBirthday());
    }
}
