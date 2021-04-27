package jerseyrest.idincrementer;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;

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

    public int getNextIndex() {
        return maxIndex++;
    }

    public int nextCourseId() {
        return maxCourseId++;
    }

    public int nextGradeId() {
        return maxGradeId++;
    }
}
