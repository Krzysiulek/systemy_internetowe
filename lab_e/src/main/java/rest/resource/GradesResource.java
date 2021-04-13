package rest.resource;

import rest.database.Repository;
import rest.models.Grade;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("students/{index}/grades")
public class GradesResource {
    Repository repository = Repository.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllGrades(@PathParam("index") int index) {
        List<Grade> grades = repository.getStudentGrades(index);
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
        Grade grade = repository.getStudentGrade(index, gradeId);
        if (repository.gradeExists(gradeId)) {
            return Response.status(Response.Status.OK)
                           .entity(grade)
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
            Grade newGrade = repository.addGrade(index, g);
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
    public Response putGrade(Grade g, @PathParam("index") int index, @PathParam("gradeId") int gradeId) {
        boolean gradeExists = repository.gradeExists(gradeId);
        if (!gradeExists) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(g)
                           .build();
        }

        Grade gradeInDataBase = repository.getGrade(gradeId);

        if (isGradeValid(g)) {
            gradeInDataBase.setValue(g.getValue());
            gradeInDataBase.setDate(g.getDate());
            gradeInDataBase.setCourse(repository.getCourse(g.getCourse()
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
        if (!repository.gradeExists(gradeId)) {
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }

        repository.deleteGrade(index, gradeId);
        return Response.status(Response.Status.NO_CONTENT)
                       .build();
    }

    private boolean isGradeValid(Grade grade) {
        return null != grade.getCourse()
                && grade.getValue() >= 2.0 && grade.getValue() <= 5.0
                && grade.getValue() % 0.5 == 0
                && repository.courseExists(grade.getCourse()
                                                .getId());
    }


    /**
     *  <grade>
     *     <course>
     *     <id>1</id>
     *     <lecturer>Nowak</lecturer>
     *     <name>Sint</name>
     *  </course>
     *      <date>2021-04-13T15:24:37.438+02:00</date>
     *      <value>3.5</value>
     *  </grade>
     */

}
