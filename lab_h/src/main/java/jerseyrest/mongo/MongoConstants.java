package jerseyrest.mongo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MongoConstants {
    public static final String MONGO_CONNECTION = "mongodb://localhost:8004";
    public static final String PACKAGE_TO_SCAN = "jerseyrest";
    public static final String STUDENTS_COLLECTION = "mongo_student";
}
