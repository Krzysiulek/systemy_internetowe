package jerseyrest.students;

import jerseyrest.courses.CoursesRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("students/{index}/grades")
public class GradesResource {
    CoursesRepository coursesRepository = CoursesRepository.getInstance();
    StudentsRepository studentsRepository = StudentsRepository.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllGrades(@PathParam("index") int index) {
        List<Grade> grades = studentsRepository.getStudentGrades(index);
        if (grades != null) {
            GenericEntity<List<Grade>> grades_entity = new GenericEntity<List<Grade>>(grades) {
            };
            return Response.status(Response.Status.OK)
                           .entity(grades_entity)
                           .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                       .build();
    }

    @GET
    @Path("{gradeId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getGrade(@PathParam("index") int index, @PathParam("gradeId") int gradeId) {
        Grade grade = studentsRepository.getStudentGrade(index, gradeId);
        if (studentsRepository.gradeExists(index, gradeId)) {
            GenericEntity<Grade> gradeEntity = new GenericEntity<>(grade) {
            };
            return Response.status(Response.Status.OK)
                           .entity(gradeEntity)
                           .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                       .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response postGrade(Grade g, @PathParam("index") int index) throws
                                                                      URISyntaxException {

        if (isGradeValid(g)) {
            Grade newGrade = studentsRepository.addStudentGrade(index, g);
            return Response.status(Response.Status.CREATED)
                           .entity(newGrade)
                           .location(new URI("/students/" + index + "/grades/" + newGrade.getId()))
                           .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                       .build();
    }

    @PUT
    @Path("{gradeId}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response putGrade(Grade newGrade, @PathParam("index") int index, @PathParam("gradeId") int gradeId) {
        boolean gradeExists = studentsRepository.gradeExists(index, gradeId);
        if (!gradeExists) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(newGrade)
                           .build();
        }

        Grade gradeInDataBase = studentsRepository.getStudentGrade(index, gradeId);

        if (isGradeValid(newGrade)) {
            gradeInDataBase.setValue(newGrade.getValue());
            gradeInDataBase.setDate(newGrade.getDate());
            gradeInDataBase.setCourse(coursesRepository.getCourse(newGrade.getCourse()
                                                                          .getId()));

            return Response.status(Response.Status.NO_CONTENT)
                           .entity(gradeInDataBase)
                           .build();
        }


        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(gradeInDataBase)
                       .build();
    }

    @DELETE
    @Path("{gradeId}")
    public Response deleteCourse(@PathParam("index") int index, @PathParam("gradeId") int gradeId) {
        if (!studentsRepository.gradeExists(index, gradeId)) {
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }

        studentsRepository.deleteStudentGrade(index, gradeId);
        return Response.status(Response.Status.NO_CONTENT)
                       .build();
    }

    private boolean isGradeValid(Grade grade) {
        return null != grade.getCourse()
                && grade.getValue() >= 2.0 && grade.getValue() <= 5.0
                && grade.getValue() % 0.5 == 0
                && coursesRepository.courseExists(grade.getCourse()
                                                       .getId());
    }


}
