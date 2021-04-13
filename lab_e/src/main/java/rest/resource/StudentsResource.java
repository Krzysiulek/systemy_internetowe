package rest.resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import rest.database.Repository;
import rest.models.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("students")
@Slf4j
public class StudentsResource {
    private Repository repository = Repository.getInstance();


    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllStudents() {
        List<Student> students = repository.getStudentsDatabase();
        log.info("Gettings all ({}) students", students.size());
        return Response.status(Response.Status.OK)
                       .entity(students)
                       .build();
    }

    @GET
    @Path("/{index}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getStudent(@PathParam("index") int index) {
        if (repository.studentExists(index)) {
            Student s = repository.getStudentByIndex(index);
            return Response.status(Response.Status.OK)
                           .entity(s)
                           .build();
        }

        log.error("Student {} not found", index);
        return Response.status(Response.Status.NOT_FOUND)
                       .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response postStudent(Student newStudent) throws
                                                    URISyntaxException {

        if (isStudentValid(newStudent)) {
            Student student = repository.addStudent(newStudent.getFirstName(),
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
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("{index}")
    public Response putStudent(Student updatedStudent, @PathParam("index") int index) {
        boolean studentExists = repository.studentExists(index);
        if (!studentExists) {
            log.info("Skipping update. Student {} doesn't exists", index);
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }

        Student studentInDataBase = repository.getStudentByIndex(index);
        log.info("Updating student {}", index);


        if (isStudentValid(updatedStudent)) {
            studentInDataBase.setFirstName(updatedStudent.getFirstName());
            studentInDataBase.setLastName(updatedStudent.getLastName());
            studentInDataBase.setBirthday(updatedStudent.getBirthday());
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
        boolean studentExists = repository.studentExists(index);
        if (!studentExists) {
            log.info("Student {} deleted", index);
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }
        repository.deleteStudent(index);
        return Response.status(Response.Status.NO_CONTENT)
                       .build();
    }

    private boolean isStudentValid(Student student) {
        return null != student.getBirthday()
                && StringUtils.isNotBlank(student.getFirstName())
                && StringUtils.isNotBlank(student.getLastName());
    }

}
