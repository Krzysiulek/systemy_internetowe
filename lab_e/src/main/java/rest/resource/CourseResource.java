package rest.resource;


import rest.database.Repository;
import rest.models.Course;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("courses")
public class CourseResource {
    Repository repository = Repository.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getAllCourses() {
        List<Course> courses = repository.getCourseDatabase();
        GenericEntity<List<Course>> courses_entity = new GenericEntity<List<Course>>(courses) {
        };
        return Response.status(Response.Status.OK)
                       .entity(courses_entity)
                       .build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getCourse(@PathParam("id") int id) {
        Course c = repository.getCourse(id);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }
        return Response.status(Response.Status.OK)
                       .entity(c)
                       .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response postCourse(Course c) throws
                                         URISyntaxException {
        boolean courseExists = repository.courseExists(c.getId());
        if (courseExists)
            return Response.status(Response.Status.FORBIDDEN)
                           .build();
        if (c.getName() != null & c.getLecturer() != null) {
            Course course = repository.addCourse(c.getName(), c.getLecturer());
            return Response.status(Response.Status.CREATED)
                           .location(new URI("/courses/" + course.getId()))
                           .entity(course)
                           .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                       .build();
    }


    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @Path("{id}")
    public Response putCourse(Course c, @PathParam("id") int id) {
        boolean courseExists = repository.courseExists(id);
        if (!courseExists) {
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }

        boolean modified = false;
        Course courseInDataBase = repository.getCourse(id);
        String coursePutName = c.getName();
        String coursePutTeacher = c.getLecturer();

        if (coursePutName != null && !coursePutName.equals(courseInDataBase.getName())) {
            courseInDataBase.setName(coursePutName);
            modified = true;
        }
        if (coursePutTeacher != null && !coursePutTeacher.equals(courseInDataBase.getLecturer())) {
            courseInDataBase.setLecturer(coursePutTeacher);
            modified = true;
        }
        if (modified)
            return Response.status(Response.Status.OK)
                           .entity(c)
                           .build();
        else
            return Response.status(Response.Status.NOT_MODIFIED)
                           .entity(c)
                           .build();
    }


    // TODO: 4/13/21 USUWAC TAKZE OCENY (czy jakos tak)
    @DELETE
    @Path("{id}")
    public Response deleteCourse(@PathParam("id") int id) {
        if (!repository.courseExists(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }


        repository.deleteCourse(id);
        return Response.status(Response.Status.NO_CONTENT)
                       .build();
    }

}
