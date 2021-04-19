package jerseyrest.students.student;

import jerseyrest.courses.CoursesRepository;
import jerseyrest.students.grade.Grade;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentsRepository {
    private static StudentsRepository repository;

    private static int indexCounter = 0;
    private static int gradesCounter = 0;

    private final List<Student> students = Collections.synchronizedList(new ArrayList<>());

    public static StudentsRepository getInstance() {
        if (repository == null) {
            repository = new StudentsRepository();
        }

        return repository;
    }

    public Student addStudent(String firstName, String lastName, Date birthDate) {
        Student s = new Student(generateStudentIndex(), firstName, lastName, birthDate);
        students.add(s);
        return s;
    }

    public void deleteStudentByIndex(int index) {
        students.removeIf(student -> student.getIndex() == index);
    }

    public boolean ifStudentExists(int index) {
        return students.stream()
                       .anyMatch(s -> s.getIndex() == index);
    }

    public Student findStudentByIndex(int index) {
        return students.stream()
                       .filter(student -> student.getIndex() == index)
                       .findFirst()
                       .orElse(null);
    }

    public Grade getStudentGrade(int index, int gradeId) {
        List<Grade> grades = getStudentGrades(index);
        return grades.stream()
                     .filter(g -> g.getId() == gradeId)
                     .findFirst()
                     .orElse(null);
    }

    public void deleteStudentGrade(int index, int gradeId) {
        Grade gradeToDelete = findStudentByIndex(index)
                .getGrades()
                .stream()
                .filter(grade -> grade.getId() == gradeId)
                .findFirst()
                .orElseThrow();

        findStudentByIndex(index)
                .deleteGrade(gradeToDelete);
    }

    public void deleteAllGradesWhere(int courseId) {
        students
                .forEach(s -> s.deleteGradeWithId(courseId));
    }

    public Grade addStudentGrade(int index, Grade grade) {
        if (!isGradeValid(grade)) {
            throw new RuntimeException("Invalid grade");
        }

        CoursesRepository coursesCoursesRepository = CoursesRepository.getInstance();

        grade.setId(createGradeId());
        grade.setCourse(coursesCoursesRepository.getCourse(grade.getCourse()
                                                                .getId()));
        findStudentByIndex(index).addGrade(grade);
        return grade;
    }

    public List<Grade> getStudentGrades(int index) {
        return findStudentByIndex(index)
                .getGrades();
    }

    public List<Student> findAllStudents() {
        return students;
    }

    public boolean gradeExists(int index, int gradeId) {
        return findStudentByIndex(index)
                .getGrades()
                .stream()
                .anyMatch(grade -> grade.getId() == gradeId);
    }

    private boolean isGradeValid(Grade grade) {
        Double value = grade.getValue();
        return value >= 2.0 && value <= 5.0 && value % 0.5 == 0;
    }


    private static int createGradeId() {
        gradesCounter++;
        return gradesCounter;
    }

    private static int generateStudentIndex() {
        indexCounter++;
        return indexCounter;
    }
}
