package jerseyrest.courses;


import jerseyrest.students.student.StudentsRepository;

import java.util.ArrayList;
import java.util.List;

public class CoursesRepository {
    private static CoursesRepository coursesRepository;

    private static int courseCounter = 0;

    private final List<Course> courseDatabase = new ArrayList<>();


    public static CoursesRepository getInstance() {
        if (coursesRepository == null) {
            coursesRepository = new CoursesRepository();
        }
        return coursesRepository;
    }

    public List<Course> findAllCourses() {
        return courseDatabase;
    }

    public Course addCourse(String name, String teacher) {
        Course c = new Course(generateCourseId(), name, teacher);
        this.courseDatabase.add(c);
        return c;
    }

    public Course getCourse(int id) {
        return this.courseDatabase.stream()
                                  .filter(c -> c.getId() == id)
                                  .findFirst()
                                  .orElse(null);
    }

    public boolean courseExists(long id) {
        return courseDatabase.stream()
                             .anyMatch(course -> course.getId() == id);
    }

    public void deleteCourse(int courseId) {
        courseDatabase.removeIf(course -> course.getId() == courseId);

        StudentsRepository.getInstance()
                          .deleteAllGradesWhere(courseId);
    }

    private static int generateCourseId() {
        courseCounter++;
        return courseCounter;
    }
}
