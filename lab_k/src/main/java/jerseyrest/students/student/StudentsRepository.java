package jerseyrest.students.student;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.experimental.filters.Filter;
import dev.morphia.query.experimental.filters.Filters;
import jerseyrest.courses.Course;
import jerseyrest.courses.CoursesRepository;
import jerseyrest.idincrementer.IncrementerUtils;
import jerseyrest.mongo.MongoClient;
import jerseyrest.students.grade.Grade;
import jerseyrest.utils.FilterUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentsRepository {
    private static final Datastore datastore = MongoClient.getDatastore();
    private static StudentsRepository repository;
    private final IncrementerUtils incrementerUtils = new IncrementerUtils();

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

    public Optional<Student> findStudentByIndexFiltered(int index, Double value, Double valueCompare) {
        Query<Student> query = datastore.find(Student.class)
                                        .filter(Filters.eq("index", index));

        if (value != null && valueCompare == null) {
            query = query.filter(Filters.elemMatch("grades", Filters.eq("value", value)));
        }


        return query
                .iterator()
                .toList()
                .stream()
                .findFirst();
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

    @SneakyThrows
    public List<Grade> getStudentGradesFiltered(int index,
                                                Double value,
                                                Double valueCompare,
                                                String date,
                                                String dateCompare,
                                                String course) {
        Optional<Student> studentByIndexFiltered = findStudentByIndexFiltered(index, value, valueCompare);
        if (studentByIndexFiltered.isPresent()) {
            var grades = studentByIndexFiltered.get()
                                               .getGrades();

            if (value != null) {
                if (valueCompare == null) {
                    grades = grades.stream()
                                   .filter(el -> el.getValue()
                                                   .equals(value))
                                   .collect(Collectors.toList());
                } else {
                    if (valueCompare == 1.0) {
                        grades = grades.stream()
                                       .filter(el -> el.getValue() >= value)
                                       .collect(Collectors.toList());
                    } else if (valueCompare == -1.0) {
                        grades = grades.stream()
                                       .filter(el -> el.getValue() <= value)
                                       .collect(Collectors.toList());
                    }
                }
            }

            if (date != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date parsedDate = sdf.parse(date);

                if (dateCompare == null) {
                    grades = grades
                            .stream()
                            .filter(el -> isSameDay(el.getDate(), parsedDate))
                            .collect(Collectors.toList());
                } else {
                    if (dateCompare.equals("1")) {
                        grades = grades
                                .stream()
                                .filter(el -> el.getDate()
                                                .after(parsedDate))
                                .collect(Collectors.toList());
                    } else if (dateCompare.equals("-1")) {
                        grades = grades
                                .stream()
                                .filter(el -> el.getDate()
                                                .before(parsedDate))
                                .collect(Collectors.toList());
                    }
                }
            }

            if (course != null) {
                grades = grades.stream()
                               .filter(el -> el.getCourse()
                                               .getId() == parseInt(course))
                               .collect(Collectors.toList());
            }

            return grades;
        }

        return Collections.emptyList();
    }

    private int parseInt(String val) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }

    @SneakyThrows
    public List<Student> findStudentsFiltered(String firstName,
                                              String lastName,
                                              String birthday,
                                              String dateComparator) {
        var filters = new ArrayList<Filter>();

        if (firstName != null) {
            filters.add(FilterUtils.containsFilter(Student.FIRST_NAME, firstName));
        }

        if (lastName != null) {
            filters.add(FilterUtils.containsFilter(Student.LAST_NAME, lastName));
        }

        if (birthday != null) {
            var date = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);

            if (dateComparator == null) {
                filters.add(Filters.eq(Student.BIRTHDAY, date));
            } else {
                if (dateComparator.equals("-1")) {
                    filters.add(Filters.lte(Student.BIRTHDAY, date));
                } else if (dateComparator.equals("1")) {
                    filters.add(Filters.gte(Student.BIRTHDAY, date));
                }
            }
        }

        Filter filter = Filters.and(filters.toArray(new Filter[0]));
        Query<Student> students = datastore.find(Student.class);

        if (!filters.isEmpty()) {
            students = students.filter(filter);
        }

        return students
                .iterator()
                .toList();
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

    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1)
                  .equals(fmt.format(date2));
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
