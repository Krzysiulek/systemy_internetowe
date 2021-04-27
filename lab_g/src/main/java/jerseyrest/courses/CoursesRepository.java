package jerseyrest.courses;


import dev.morphia.Datastore;
import jerseyrest.mongo.MongoClient;
import jerseyrest.students.student.StudentsRepository;

import java.util.ArrayList;
import java.util.List;

public class CoursesRepository {
    private static CoursesRepository coursesRepository;
    private static int courseCounter = 0;
    private final Datastore datastore = MongoClient.getDatastore();

    public static CoursesRepository getInstance() {
        if (coursesRepository == null) {
            coursesRepository = new CoursesRepository();
        }
        return coursesRepository;
    }

    public List<Course> findAllCourses() {
        return datastore.find(Course.class)
                        .iterator()
                        .toList();
    }

    public Course addCourse(String name, String teacher) {
        Course c = new Course(generateCourseId(), name, teacher);
        datastore.save(c);
        return c;
    }

    public Course getCourse(int id) {
        return findAllCourses().stream()
                               .filter(c -> c.getId() == id)
                               .findFirst()
                               .orElse(null);
    }

    public boolean courseExists(long id) {
        return findAllCourses().stream()
                               .anyMatch(course -> course.getId() == id);
    }

    public void deleteCourse(int courseId) {
        datastore.delete(getCourse(courseId));

    }

    private static int generateCourseId() {
        courseCounter++;
        return courseCounter;
    }
}
