package jerseyrest.students;

import jerseyrest.students.student.Student;
import jerseyrest.students.student.StudentsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("students")
@Slf4j
public class StudentsResource {
    private final StudentsRepository studentsRepository = StudentsRepository.getInstance();


    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getAllStudents(@QueryParam("firstName") String firstName,
                                   @QueryParam("lastName") String lastName,
                                   @QueryParam("birthday") String birthday,
                                   @QueryParam("birthdayCompare") String birthdayCompare) {
        List<Student> students = studentsRepository.findStudentsFiltered(firstName,
                                                                         lastName,
                                                                         birthday,
                                                                         birthdayCompare);
        log.info("Gettings all ({}) students", students.size());

        GenericEntity<List<Student>> entities = new GenericEntity<>(students) {
        };

        return Response.status(Response.Status.OK)
                       .entity(entities)
                       .build();
    }

    @GET
    @Path("/{index}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudent(@PathParam("index") int index) {
        if (studentsRepository.ifStudentExists(index)) {
            Student student = studentsRepository.findStudentByIndex(index);
            return Response.status(Response.Status.OK)
                           .entity(student)
                           .build();
        }

        log.error("Student {} not found", index);
        return Response.status(Response.Status.NOT_FOUND)
                       .build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postStudent(Student newStudent) throws
                                                    URISyntaxException {
        if (isStudentValid(newStudent)) {
            Student student = studentsRepository.addStudent(newStudent.getFirstName(),
                                                            newStudent.getLastName(),
                                                            newStudent.getBirthday());
            log.info("Adding student {}", student.toString());
            return Response.status(Response.Status.CREATED)
                           .location(new URI("/students/" + student.getIndex()))
                           .entity(student)
                           .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(newStudent)
                       .build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("{index}")
    public Response putStudent(Student updatedStudent, @PathParam("index") int index) {
        boolean studentExists = studentsRepository.ifStudentExists(index);
        if (!studentExists) {
            log.info("Skipping update. Student {} doesn't exists", index);
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }

        Student studentInDataBase = studentsRepository.findStudentByIndex(index);
        log.info("Updating student {}", index);


        if (isStudentValid(updatedStudent)) {
            studentsRepository.updateStudent(studentInDataBase, updatedStudent);
            log.info("Student {} updated", index);
            return Response.status(Response.Status.NO_CONTENT)
                           .entity(studentInDataBase)
                           .build();
        }

        log.error("Bad request. Student {} not updated.", index);
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(updatedStudent)
                       .build();
    }

    @DELETE
    @Path("{index}")
    public Response deleteStudent(@PathParam("index") int index) {
        boolean studentExists = studentsRepository.ifStudentExists(index);
        if (!studentExists) {
            log.info("Student {} deleted", index);
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }
        studentsRepository.deleteStudentByIndex(index);
        return Response.status(Response.Status.NO_CONTENT)
                       .build();
    }

    private boolean isStudentValid(Student student) {
        return null != student.getBirthday()
                && StringUtils.isNotBlank(student.getFirstName())
                && StringUtils.isNotBlank(student.getLastName());
    }

}
