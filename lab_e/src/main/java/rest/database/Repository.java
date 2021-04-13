package rest.database;


import lombok.Getter;
import rest.models.Course;
import rest.models.Grade;
import rest.models.Student;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Repository {
    @Getter
    private final List<Student> studentsDatabase = new ArrayList<>();
    @Getter
    private final List<Grade> gradesDatabase = new ArrayList<>();
    @Getter
    private final List<Course> courseDatabase = new ArrayList<>();
    private static Repository repository;
    private static int indexCounter = 0;
    private static int gradesCounter = 0;


    public static Repository getInstance() {
        if (repository == null) {
            synchronized (Repository.class) {
                if (repository == null) {
                    repository = new Repository();
                }
            }
        }
        return repository;
    }

    private Repository() {
        addStudent("Krzysztof", "Czarnecki", new Date());
        addStudent("Jan", "Kowalski", new Date());
        Course c1 = addCourse("Sint", "Nowak");
        Course c2 = addCourse("MISIO", "Bandi");
        addGrade(1, 4.0, c1);
        addGrade(2, 4.5, c2);
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

    public int createCourseIndex() {
        return this.courseDatabase.size() + 1;
    }


    public Grade getStudentGrade(int index, int gradeId) {
        List<Grade> grades = getStudentGrades(index);
        for (Grade g : grades) {
            if (g.getId() == gradeId) {
                return g;
            }
        }
        return null;
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

        grade.setCourse(repository.getCourse(grade.getCourse()
                                                  .getId()));
        getStudentByIndex(index).addGrade(grade);
        grade.setId(createGradeId());
        this.gradesDatabase.add(grade);
        return grade;
    }

    public boolean gradeExists(int gradeId) {
        for (Grade g : this.gradesDatabase) {
            if (g.getId() == gradeId) {
                return true;
            }
        }
        return false;
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

    private static int createStudentIndex() {
        indexCounter++;
        return indexCounter;
    }

    private static int createGradeId() {
        gradesCounter++;
        return gradesCounter;
    }


    public boolean studentExists(int index) {
        for (Student s : this.studentsDatabase) {
            if (s.getIndex() == index) {
                return true;
            }
        }
        return false;
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
}
