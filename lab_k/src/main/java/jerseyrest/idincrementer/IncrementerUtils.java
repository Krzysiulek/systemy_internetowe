package jerseyrest.idincrementer;

import dev.morphia.Datastore;
import dev.morphia.ModifyOptions;
import dev.morphia.query.experimental.updates.UpdateOperators;
import jerseyrest.mongo.MongoClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IncrementerUtils {
    private static final Datastore datastore = MongoClient.getDatastore();

    public IncrementerUtils() {
        boolean containt = datastore.find(IdIncrementer.class)
                                    .count() > 0;

        if (!containt) {
            datastore.save(new IdIncrementer());
        }
    }

    public int createGradeId() {
        return increment("maxGradeId").getMaxGradeId();
    }

    public int createIndexId() {
        return increment("maxIndex").getMaxIndex();
    }

    public int createCourseId() {
        return increment("maxCourseId").getMaxCourseId();
    }

    private IdIncrementer increment(String fieldName) {
        return datastore.find(IdIncrementer.class)
                        .modify(UpdateOperators.inc(fieldName))
                        .execute(new ModifyOptions());
    }
}
