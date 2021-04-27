package jerseyrest.students.student;

import dev.morphia.Datastore;
import jerseyrest.courses.Course;
import jerseyrest.courses.CoursesRepository;
import jerseyrest.idincrementer.IdIncrementer;
import jerseyrest.idincrementer.IncrementerUtils;
import jerseyrest.mongo.MongoClient;
import jerseyrest.students.grade.Grade;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentsRepository {
    private static StudentsRepository repository;

    private static final Datastore datastore = MongoClient.getDatastore();
    private IncrementerUtils incrementerUtils = new IncrementerUtils();

    public static StudentsRepository getInstance() {
        if (repository == null) {
            repository = new StudentsRepository();
        }

        return repository;
    }

    public Student addStudent(String firstName, String lastName, Date birthDate) {
        Student s = new Student(incrementerUtils.createIndexId(), firstName, lastName, birthDate);
        datastore.save(s);
        return s;
    }

    public void deleteStudentByIndex(int index) {
        Student student = findAllStudents().stream()
                                           .filter(s -> s.getIndex() == index)
                                           .findFirst()
                                           .get();

        datastore.delete(student);
    }

    public boolean ifStudentExists(int index) {
        return findAllStudents().stream()
                                .anyMatch(s -> s.getIndex() == index);
    }

    public Student findStudentByIndex(int index) {
        return findAllStudents().stream()
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
        Student student = findStudentByIndex(index);
        Grade studentGrade = getStudentGrade(index, gradeId);
        student.deleteGrade(studentGrade);
        datastore.save(student);
    }

    public Grade addStudentGrade(int index, Grade grade) {
        if (!isGradeValid(grade)) {
            throw new RuntimeException("Invalid grade");
        }

        CoursesRepository coursesCoursesRepository = CoursesRepository.getInstance();

        grade.setId(incrementerUtils.createGradeId());
        grade.setStudentIndex(index);
        grade.getLinks()
             .clear();
        grade.setCourse(coursesCoursesRepository.getCourse(grade.getCourse()
                                                                .getId()));
        Student studentByIndex = findStudentByIndex(index);
        studentByIndex.addGrade(grade);
        datastore.save(studentByIndex);
        return grade;
    }

    public List<Grade> getStudentGrades(int index) {
        return findStudentByIndex(index)
                .getGrades();
    }

    public List<Student> findAllStudents() {
        return datastore.find(Student.class)
                        .iterator()
                        .toList();
    }

    public boolean gradeExists(int index, int gradeId) {
        return findStudentByIndex(index)
                .getGrades()
                .stream()
                .anyMatch(grade -> grade.getId() == gradeId);
    }

    private boolean isGradeValid(Grade grade) {
        double value = grade.getValue();
        return value >= 2.0 && value <= 5.0 && value % 0.5 == 0;
    }

    public void updateStudent(Student studentInDataBase, Student updatedStudent) {
        studentInDataBase.setFirstName(updatedStudent.getFirstName());
        studentInDataBase.setLastName(updatedStudent.getLastName());
        studentInDataBase.setBirthday(updatedStudent.getBirthday());

        datastore.save(studentInDataBase);
    }

    public void updateGrade(int index, int gradeId, Grade newGrade, Course newCourse) {
        Student student = findStudentByIndex(index);
        var grade = student.getGrades()
                           .stream()
                           .filter(g -> g.getId() == gradeId)
                           .findFirst()
                           .get();

        grade.setValue(newGrade.getValue());
        grade.setDate(newGrade.getDate());
        grade.setCourse(newCourse);

        datastore.save(student);
    }

    public void deleteGradesWhereCourseId(int id) {
        List<Student> allStudents = findAllStudents();

        allStudents
                .forEach(student -> {
                    student.deleteGradeWithId(id);
                    datastore.save(student);
                });
    }
}
