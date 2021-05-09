package jerseyrest.courses;


import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filter;
import dev.morphia.query.experimental.filters.Filters;
import jerseyrest.idincrementer.IncrementerUtils;
import jerseyrest.mongo.MongoClient;
import jerseyrest.utils.FilterUtils;

import java.util.ArrayList;
import java.util.List;

public class CoursesRepository {
    private static CoursesRepository coursesRepository;
    private final Datastore datastore = MongoClient.getDatastore();
    private IncrementerUtils incrementerUtils = new IncrementerUtils();

    public static CoursesRepository getInstance() {
        if (coursesRepository == null) {
            coursesRepository = new CoursesRepository();
        }
        return coursesRepository;
    }

    public List<Course> findAllCourses(String lecturer, String name) {
        var filtersList = new ArrayList<Filter>();

        if (lecturer != null) {
            filtersList.add(FilterUtils.containsFilter(Course.LECTURER, lecturer));
        }

        if (name != null) {
            filtersList.add(FilterUtils.containsFilter(Course.NAME, name));
        }


        var courses = datastore.find(Course.class);
        if (!filtersList.isEmpty()) {
            Filter filters = Filters.and(filtersList.toArray(new Filter[0]));
            courses = courses.filter(filters);
        }

        return courses
                .iterator()
                .toList();
    }

    public Course addCourse(String name, String teacher) {
        Course c = new Course(incrementerUtils.createCourseId(), name, teacher);
        datastore.save(c);
        return c;
    }

    public Course getCourse(int id) {
        return findAllCourses(null, null).stream()
                                         .filter(c -> c.getId() == id)
                                         .findFirst()
                                         .orElse(null);
    }

    public boolean courseExists(long id) {
        return findAllCourses(null, null).stream()
                                         .anyMatch(course -> course.getId() == id);
    }

    public void deleteCourse(int courseId) {
        datastore.delete(getCourse(courseId));

    }
}
