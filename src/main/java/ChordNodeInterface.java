
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/node")
public interface ChordNodeInterface {

    // Finds the successor node for a given identifier.
    int findSuccessor(String identifier);

    // finds the predecessor of a given node
    @GET
    @Path("/findPredecessor")
    int findPredecessor(@QueryParam("id") int id);

    // Finds the closest preceding node in the finger table for a given identifier.
    @GET
    @Path("/closestPrecedingFinger")
    int closestPrecedingFinger(@QueryParam("id") int id);

    // Gets the immediate successor of this node.
    int getSuccessor();

    // Sets the immediate successor of this node.
    void setSuccessor(int successor);

    // Gets the immediate predecessor of this node.
    int getPredecessor();

    // Sets the immediate predecessor of this node.
    void setPredecessor(int predecessor);

    @GET
    @Path("/getM")
    int getM();

    @GET
    @Path("/getFingerNode")
    @Consumes(MediaType.APPLICATION_JSON)
    int getFingerNode(@QueryParam("fingerIndex") int fingerIndex);

    @GET
    @Path("/setFingerNode")
    @Consumes(MediaType.APPLICATION_JSON)
    void setFingerNode(@QueryParam("fingerIndex") int fingerIndex, @QueryParam("node") int node);

    // Joins the Chord ring using another node as an entry point.
    @POST
    @Path("/join")
    @Consumes(MediaType.APPLICATION_JSON)
    void join(int node);

    // Notifies another node that this node should be considered its predecessor.
    void notify(int potentialPredecessor);

    // Leaves the Chord ring, transferring keys and updating neighboring nodes.
    void leave();

    // checks if another node is reachable.
    @GET
    @Path("/checkIfNode")
    @Consumes(MediaType.APPLICATION_JSON)
    void checkIfNode(@QueryParam("id") int id);

    // send message to another node
    @GET
    @Path("/sendMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    void sendMessage(@QueryParam("id") int id, @QueryParam("message") String message);

    @GET
    @Path("/check")
    int check();

    // Updates the finger table based on the current state of the Chord ring.
    @GET
    @Path("/calculateFingerTable")
    void calculateFingerTable();

    // asks a node for closest preceding finger to id
    @GET
    @Path("/askClosestPrecedingFinger")
    int askClosestPrecedingFinger(@QueryParam("id") int id, @QueryParam("node") int node);

    // asks a node for its successor
    @GET
    @Path("/askSuccessor")
    int askSuccessor(@QueryParam("node") int node);

    @GET
    @Path("/printNode")
    String printNode();

    @GET
    @Path("/add")
    int add(@QueryParam("value") String value);

    @GET
    @Path("/askToAdd")
    @Consumes(MediaType.APPLICATION_JSON)
    void askToAdd(@QueryParam("id") int id, @QueryParam("value") String value);

    @GET
    @Path("/get")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    String get(@QueryParam("key") int key);

    @GET
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    void remove(@QueryParam("key") int key);
}
