package jerseyrest.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.PojoCodecProvider;

import static jerseyrest.mongo.MongoConstants.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MongoClient {
    private static Datastore datastore;

    public static Datastore getDatastore() {
        if (datastore == null) {
            var pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                                                   fromProviders(PojoCodecProvider.builder()
                                                                                  .automatic(true)
                                                                                  .build()));

            MongoClientSettings settings = MongoClientSettings.builder()
                                                              .codecRegistry(pojoCodecRegistry)
                                                              .applyConnectionString(new ConnectionString(
                                                                      MONGO_CONNECTION))
                                                              .build();

            datastore = Morphia
                    .createDatastore(MongoClients.create(settings), STUDENTS_COLLECTION);
            datastore.getMapper()
                     .mapPackage(PACKAGE_TO_SCAN);
        }

        return datastore;
    }
}
