package jerseyrest.students.grade;

import jerseyrest.courses.Course;
import jerseyrest.courses.CoursesRepository;
import jerseyrest.students.student.StudentsRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("students/{index}/grades")
public class GradesResource {
    private final CoursesRepository coursesRepository = CoursesRepository.getInstance();
    private final StudentsRepository studentsRepository = StudentsRepository.getInstance();

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAllGrades(@PathParam("index") int index,
                                 @QueryParam("value") Double value,
                                 @QueryParam("valueCompare") Double valueCompare,
                                 @QueryParam("date") String date,
                                 @QueryParam("dateCompare") String dateCompare,
                                 @QueryParam("course") String course) {
        List<Grade> grades = studentsRepository.getStudentGradesFiltered(index,
                                                                         value,
                                                                         valueCompare,
                                                                         date,
                                                                         dateCompare,
                                                                         course);
        if (grades != null) {
            GenericEntity<List<Grade>> gradesEntity = new GenericEntity<>(grades) {
            };
            return Response.status(Response.Status.OK)
                           .entity(gradesEntity)
                           .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                       .build();
    }

    @GET
    @Path("{gradeId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
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
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
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
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putGrade(Grade newGrade, @PathParam("index") int index, @PathParam("gradeId") int gradeId) {
        newGrade.getLinks()
                .clear();
        boolean gradeExists = studentsRepository.gradeExists(index, gradeId);
        if (!gradeExists) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(newGrade)
                           .build();
        }

        Grade gradeInDataBase = studentsRepository.getStudentGrade(index, gradeId);

        if (isGradeValid(newGrade)) {
            Course newCourse = coursesRepository.getCourse(newGrade.getCourse()
                                                                   .getId());
            studentsRepository.updateGrade(index, gradeId, newGrade, newCourse);

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
        return grade.getValue() >= 2.0 && grade.getValue() <= 5.0 && grade.getValue() % 0.5 == 0 && coursesRepository.courseExists(
                grade.getCourse()
                     .getId());
    }


}
