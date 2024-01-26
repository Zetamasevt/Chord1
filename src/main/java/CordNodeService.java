import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
@Path("/node")
public class CordNodeService implements ChordNodeInterface{
    @Override
    @GET
    @Path("/findSuccessor")
    @Produces(MediaType.APPLICATION_JSON)
    public NodeRef findSuccessor(String identifier) {
        return null;
    }

    @Override
    @GET
    @Path("/closestPrecedingFinger")
    @Produces(MediaType.APPLICATION_JSON)
    public NodeRef closestPrecedingFinger(String identifier) {
        return null;
    }

    @Override
    @GET
    @Path("/closestPrecedingFinger")
    @Produces(MediaType.APPLICATION_JSON)
    public NodeRef getSuccessor() {
        return null;
    }

    @Override
    @GET
    @Path("/closestPrecedingFinger")
    @Produces(MediaType.APPLICATION_JSON)
    public void setSuccessor(NodeRef successor) {

    }

    @Override
    @GET
    @Path("/closestPrecedingFinger")
    @Produces(MediaType.APPLICATION_JSON)
    public NodeRef getPredecessor() {
        return null;
    }

    @Override
    @PUT
    @Path("/setPredecessor")
    @Consumes(MediaType.APPLICATION_JSON)
    public void setPredecessor(NodeRef predecessor) {

    }

    @Override
    @PUT
    @Path("/updateFingerTable")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateFingerTable() {

    }

    @Override
    @POST
    @Path("/join")
    @Consumes(MediaType.APPLICATION_JSON)
    public void join(NodeRef entryPoint) {

    }

    @Override
    @POST
    @Path("/notify")
    @Consumes(MediaType.APPLICATION_JSON)
    public void notify(NodeRef potentialPredecessor) {

    }

    @Override
    @DELETE
    @Path("/leave")
    public void leave() {

    }
}
