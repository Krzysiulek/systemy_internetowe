package jerseyrest.students.student;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import jerseyrest.courses.CoursesRepository;
import jerseyrest.students.grade.Grade;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static jerseyrest.mongo.MongoConstants.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentsRepository {
    private static StudentsRepository repository;

    private static int indexCounter = 0;
    private static int gradesCounter = 0;
    private static Datastore studentsDatastore;

    public static StudentsRepository getInstance() {
        if (repository == null) {
            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                                                             fromProviders(PojoCodecProvider.builder()
                                                                                            .automatic(true)
                                                                                            .build()));

            MongoClientSettings settings = MongoClientSettings.builder()
                                                              .codecRegistry(pojoCodecRegistry)
                                                              .applyConnectionString(new ConnectionString(
                                                                      MONGO_CONNECTION))
                                                              .build();

            studentsDatastore = Morphia
                    .createDatastore(MongoClients.create(settings), STUDENTS_COLLECTION);
            studentsDatastore.getMapper()
                             .mapPackage(PACKAGE_TO_SCAN);

            studentsDatastore.getDatabase()
                             .drop();

            repository = new StudentsRepository();
            indexCounter = repository.findAllStudents()
                                     .stream()
                                     .mapToInt(Student::getIndex)
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
        // TODO: 27/04/2021
//        students
//                .forEach(s -> s.deleteGradeWithId(courseId));
    }

    public Grade addStudentGrade(int index, Grade grade) {
        if (!isGradeValid(grade)) {
            throw new RuntimeException("Invalid grade");
        }

        CoursesRepository coursesCoursesRepository = CoursesRepository.getInstance();

        grade.setId(createGradeId());
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
}
