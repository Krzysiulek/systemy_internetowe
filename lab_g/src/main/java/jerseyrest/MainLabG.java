package jerseyrest;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import jerseyrest.courses.Course;
import jerseyrest.courses.CoursesRepository;
import jerseyrest.students.grade.Grade;
import jerseyrest.students.student.Student;
import jerseyrest.students.student.StudentsRepository;
import jerseyrest.utils.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.Date;


@Slf4j
public class MainLabG {
    private static final String BASE_URI = "http://localhost:8000/";

    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig()
                .packages("jerseyrest")
                .register(DeclarativeLinkingFeature.class)
                .register(new ExceptionHandler());

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }


    public static void main(String[] args) {
//        initMongoDb();
        initRepositories();
        startServer();
        log.info("Server started on {}", BASE_URI);
    }

//    private static void initMongoDb() {
//        var mongoClient = MongoClients.create("mongodb://localhost:8004");
//        mongoClient.getDatabase("test");
//
//        final var datastore = Morphia.createDatastore(mongoClient, "test");
//
//        // tell Morphia where to find your classes
//        // can be called multiple times with different packages or classes
//        datastore.getMapper()
//                 .mapPackage("jerseyrest");
//
//        datastore.ensureIndexes();
//    }

    private static void initRepositories() {
        CoursesRepository coursesRepository = CoursesRepository.getInstance();
        StudentsRepository instance = StudentsRepository.getInstance();

        Course course1 = coursesRepository.addCourse("SINT", "JanNowak");
        Course course2 = coursesRepository.addCourse("MISIO", "Kowalksi");

        Student student1 = instance.addStudent("Kris", "Brown", new Date());
        Student student2 = instance.addStudent("Jan", "Nowak", new Date());

        Grade grade1 = new Grade(0, student1.getIndex(), 3.5, new Date(), course1);
        Grade grade2 = new Grade(0, student2.getIndex(), 4.5, new Date(), course2);

        instance.addStudentGrade(student1.getIndex(), grade1);
        instance.addStudentGrade(student2.getIndex(), grade2);
    }
}

