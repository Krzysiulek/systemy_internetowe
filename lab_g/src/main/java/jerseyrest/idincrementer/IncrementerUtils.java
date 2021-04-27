package jerseyrest.idincrementer;

import dev.morphia.Datastore;
import jerseyrest.mongo.MongoClient;

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
        datastore.save(first);
        return gradeId;
    }

    public int createIndexId() {
        IdIncrementer first = datastore.find(IdIncrementer.class)
                                       .first();
        var index = first.getNextIndex();
        datastore.save(first);
        return index;
    }

    public int createCourseId() {
        IdIncrementer first = datastore.find(IdIncrementer.class)
                                       .first();
        var id = first.nextCourseId();
        datastore.save(first);
        return id;
    }
}
