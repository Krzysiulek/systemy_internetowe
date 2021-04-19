package jerseyrest.courses;


import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("courses")
public class CourseResource {
    private CoursesRepository coursesRepository = CoursesRepository.getInstance();

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAllCourses() {
        List<Course> courses = coursesRepository.findAllCourses();
        GenericEntity<List<Course>> entities = new GenericEntity<>(courses) {
        };

        return Response.status(Response.Status.OK)
                       .entity(entities)
                       .build();
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCourse(@PathParam("id") int id) {
        Course c = coursesRepository.getCourse(id);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }
        return Response.status(Response.Status.OK)
                       .entity(c)
                       .build();
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postCourse(Course c) throws
                                         URISyntaxException {
        boolean courseExists = coursesRepository.courseExists(c.getId());
        if (courseExists) {
            return Response.status(Response.Status.FORBIDDEN)
                           .build();
        }

        if (c.getName() != null && c.getLecturer() != null) {
            Course course = coursesRepository.addCourse(c.getName(), c.getLecturer());
            return Response.status(Response.Status.CREATED)
                           .location(new URI("/courses/" + course.getId()))
                           .entity(course)
                           .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                       .build();
    }


    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{id}")
    public Response putCourse(Course c, @PathParam("id") int id) {
        boolean courseExists = coursesRepository.courseExists(id);
        if (!courseExists) {
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }

        Course courseInDataBase = coursesRepository.getCourse(id);

        if (isValid(c)) {
            courseInDataBase.setLecturer(c.getLecturer());
            courseInDataBase.setName(c.getName());
            return Response.status(Response.Status.OK)
                           .entity(c)
                           .build();
        }

        return Response.status(Response.Status.NOT_MODIFIED)
                       .entity(c)
                       .build();
    }


    @DELETE
    @Path("{id}")
    public Response deleteCourse(@PathParam("id") int id) {
        if (!coursesRepository.courseExists(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                           .build();
        }

        coursesRepository.deleteCourse(id);
        return Response.status(Response.Status.NO_CONTENT)
                       .build();
    }

    private boolean isValid(Course courseInDataBase) {
        return courseInDataBase.getLecturer() != null && courseInDataBase.getName() != null;
    }

}
