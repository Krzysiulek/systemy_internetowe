package jerseyrest.database;


import lombok.Getter;
import jerseyrest.models.Course;
import jerseyrest.models.Grade;
import jerseyrest.models.Student;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Repository {
    private static Repository repository;

    private static int indexCounter = 0;
    private static int gradesCounter = 0;
    private static int courseCounter = 0;


    @Getter
    private final List<Student> studentsDatabase = new ArrayList<>();
    @Getter
    private final List<Grade> gradesDatabase = new ArrayList<>();
    @Getter
    private final List<Course> courseDatabase = new ArrayList<>();


    private Repository() {
        addStudent("Krzysztof", "Czarnecki", new Date());
        addStudent("Jan", "Kowalski", new Date());

        Course c1 = addCourse("SINT", "Nowak");
        Course c2 = addCourse("MISIO", "Bandi");

        addGrade(1, 4.0, c1);
        addGrade(2, 4.5, c2);
    }


    public static Repository getInstance() {
        synchronized (Repository.class) {
            if (repository == null) {
                repository = new Repository();
            }
        }
        return repository;
    }

    public Course addCourse(String name, String teacher) {
        Course c = new Course(createCourseIndex(), name, teacher);
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


    public Grade getStudentGrade(int index, int gradeId) {
        List<Grade> grades = getStudentGrades(index);
        return grades.stream()
                     .filter(g -> g.getId() == gradeId)
                     .findFirst()
                     .orElse(null);
    }

    public Grade addGrade(int index, Double gradeVal, Course c) {
        if ((gradeVal < 2.0) && (gradeVal > 5.0) && (gradeVal % 0.5 != 0)) {
            throw new RuntimeException("Invalid grade");
        }

        Grade g = new Grade(createGradeId(), gradeVal, new Date(), c);
        getStudentByIndex(index).addGrade(g);
        this.gradesDatabase.add(g);
        return g;
    }

    public Grade addGrade(int index, Grade grade) {
        Double gradeVal = grade.getValue();

        if ((gradeVal < 2.0) && (gradeVal > 5.0) && (gradeVal % 0.5 != 0)) {
            throw new RuntimeException("Invalid grade");
        }

        grade.setId(createGradeId());
        grade.setCourse(repository.getCourse(grade.getCourse()
                                                  .getId()));
        getStudentByIndex(index).addGrade(grade);
        gradesDatabase.add(grade);
        return grade;
    }

    public boolean gradeExists(int gradeId) {
        return this.gradesDatabase.stream()
                                  .anyMatch(g -> g.getId() == gradeId);
    }

    public Grade getGrade(int gradeId) {
        return this.gradesDatabase.stream()
                                  .filter(g -> g.getId() == gradeId)
                                  .findFirst()
                                  .orElse(null);
    }

    public void deleteGrade(int index, int id) {
        getStudentByIndex(index).deleteGrade(getStudentGrade(index, id));
        gradesDatabase.removeIf(grade -> grade.getId() == id);
    }

    public boolean studentExists(int index) {
        return studentsDatabase.stream()
                               .anyMatch(s -> s.getIndex() == index);
    }

    public Student addStudent(String name, String surname, Date birthDate) {
        Student s = new Student(createStudentIndex(), name, surname, birthDate);
        studentsDatabase.add(s);
        return s;
    }

    public Student getStudentByIndex(int index) {
        return studentsDatabase.stream()
                               .filter(student -> student.getIndex() == index)
                               .findFirst()
                               .orElse(null);
    }

    public void deleteStudent(int index) {
        studentsDatabase.removeIf(student -> student.getIndex() == index);
    }

    public void deleteCourse(int id) {
        courseDatabase.removeIf(course -> course.getId() == id);
        gradesDatabase.removeIf(grade -> grade.getCourse()
                                              .getId() == id);
    }

    public List<Grade> getStudentGrades(int index) {
        return getStudentByIndex(index).getGrades();
    }

    private static int createCourseIndex() {
        courseCounter++;
        return courseCounter;
    }

    private static int createStudentIndex() {
        indexCounter++;
        return indexCounter;
    }

    private static int createGradeId() {
        gradesCounter++;
        return gradesCounter;
    }
}
