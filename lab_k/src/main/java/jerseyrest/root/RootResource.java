package jerseyrest.root;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
public class RootResource {


    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getRoot() {
        Root root = new Root();
        GenericEntity<Root> entities = new GenericEntity<>(root) {
        };

        return Response.status(Response.Status.OK)
                       .entity(entities)
                       .build();
    }
}
