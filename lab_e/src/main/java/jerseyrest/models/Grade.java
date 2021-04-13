package jerseyrest.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "grade")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    private int id;
    private Double value;
    private Date date;
    private Course course;
}
