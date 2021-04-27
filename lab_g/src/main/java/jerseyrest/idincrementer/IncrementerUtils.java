package jerseyrest.idincrementer;

import dev.morphia.Datastore;
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
        IdIncrementer first = datastore.find(IdIncrementer.class)
                                       .first();
        var gradeId = first.nextGradeId();
        log.info("Next gradeId {}", gradeId);
        datastore.save(first);
        return gradeId;
    }

    public int createIndexId() {
        IdIncrementer first = datastore.find(IdIncrementer.class)
                                       .first();
        var index = first.getNextIndex();
        log.info("Next index {}", index);
        datastore.save(first);
        return index;
    }

    public int createCourseId() {
        IdIncrementer first = datastore.find(IdIncrementer.class)
                                       .first();
        var id = first.nextCourseId();
        log.info("Next courseId {}", id);
        datastore.save(first);
        return id;
    }
}
