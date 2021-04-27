package jerseyrest.students.student;

import dev.morphia.Datastore;
import jerseyrest.courses.Course;
import jerseyrest.courses.CoursesRepository;
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

    private static int indexCounter = 0;
    private static int gradesCounter = 0;
    private static final Datastore studentsDatastore = MongoClient.getDatastore();

    public static StudentsRepository getInstance() {
        if (repository == null) {
            repository = new StudentsRepository();
            indexCounter = repository.findAllStudents()
                                     .stream()
                                     .mapToInt(Student::getIndex)
                                     .max()
                                     .orElse(0) + 1;

            gradesCounter = repository.findAllStudents()
                                      .stream()
                                      .map(Student::getGrades)
                                      .flatMap(Collection::stream)
                                      .mapToInt(Grade::getId)
                                      .max()
                                      .orElse(0) + 1;

        }

        return repository;
    }

    private static int createGradeId() {
        gradesCounter++;
        return gradesCounter;
    }

    private static int generateStudentIndex() {
        indexCounter++;
        return indexCounter;
    }

    public Student addStudent(String firstName, String lastName, Date birthDate) {
        Student s = new Student(generateStudentIndex(), firstName, lastName, birthDate);
        studentsDatastore.save(s);
        return s;
    }

    public void deleteStudentByIndex(int index) {
        Student student = findAllStudents().stream()
                                           .filter(s -> s.getIndex() == index)
                                           .findFirst()
                                           .get();

        studentsDatastore.delete(student);
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
        studentsDatastore.save(student);
    }

    public Grade addStudentGrade(int index, Grade grade) {
        if (!isGradeValid(grade)) {
            throw new RuntimeException("Invalid grade");
        }

        CoursesRepository coursesCoursesRepository = CoursesRepository.getInstance();

        grade.setId(createGradeId());
        grade.setStudentIndex(index);
        grade.getLinks()
             .clear();
        grade.setCourse(coursesCoursesRepository.getCourse(grade.getCourse()
                                                                .getId()));
        Student studentByIndex = findStudentByIndex(index);
        studentByIndex.addGrade(grade);
        studentsDatastore.save(studentByIndex);
        return grade;
    }

    public List<Grade> getStudentGrades(int index) {
        return findStudentByIndex(index)
                .getGrades();
    }

    public List<Student> findAllStudents() {
        return studentsDatastore.find(Student.class)
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

        studentsDatastore.save(studentInDataBase);
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

        studentsDatastore.save(student);
    }

    public void deleteGradesWhereCourseId(int id) {
        List<Student> allStudents = findAllStudents();

        allStudents
                .forEach(student -> {
                    student.deleteGradeWithId(id);
                    studentsDatastore.save(student);
                });
    }
}
