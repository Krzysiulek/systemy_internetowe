package jerseyrest;

import jerseyrest.courses.Course;
import jerseyrest.students.Grade;
import jerseyrest.students.Student;
import jerseyrest.courses.CoursesRepository;
import jerseyrest.students.StudentsRepository;
import jerseyrest.utils.ExceptionHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.Date;


public class Main {
    public static final String BASE_URI = "http://localhost:8000/";


    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("jerseyrest");
        rc.register(new ExceptionHandler());
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }


    public static void main(String[] args) throws
                                           IOException {
        initRepositories();

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                                                 + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }


    private static void initRepositories() {
        CoursesRepository coursesRepository = CoursesRepository.getInstance();
        StudentsRepository instance = StudentsRepository.getInstance();

        Course course1 = coursesRepository.addCourse("SINT", "JanNowak");
        Course course2 = coursesRepository.addCourse("MISIO", "Kowalksi");

        Student student1 = instance.addStudent("Kris", "Brown", new Date());
        Student student2 = instance.addStudent("Jan", "Nowak", new Date());

        Grade grade1 = new Grade(0, 3.5, new Date(), course1);
        Grade grade2 = new Grade(0, 4.5, new Date(), course2);

        instance.addStudentGrade(student1.getIndex(), grade1);
        instance.addStudentGrade(student2.getIndex(), grade2);
    }
}

