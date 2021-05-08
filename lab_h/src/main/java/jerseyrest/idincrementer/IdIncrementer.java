package jerseyrest.idincrementer;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;

@AllArgsConstructor
@Data
@Entity("incrementer")
public class IdIncrementer {
    @Id
    private ObjectId objectId;
    private int maxIndex;
    private int maxCourseId;
    private int maxGradeId;

    public IdIncrementer() {
        maxGradeId = 1;
        maxIndex = 1;
        maxCourseId = 1;
    }
}
