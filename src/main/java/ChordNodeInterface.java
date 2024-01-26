
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


public interface ChordNodeInterface {

    // Finds the successor node for a given identifier.
    NodeRef findSuccessor(String identifier);

    // Finds the closest preceding node in the finger table for a given identifier.
    NodeRef closestPrecedingFinger(String identifier);

    // Gets the immediate successor of this node.
    NodeRef getSuccessor();

    // Sets the immediate successor of this node.
    void setSuccessor(NodeRef successor);

    // Gets the immediate predecessor of this node.
    NodeRef getPredecessor();

    // Sets the immediate predecessor of this node.
    void setPredecessor(NodeRef predecessor);

    // Updates the finger table based on the current state of the Chord ring.
    void updateFingerTable();

    // Joins the Chord ring using another node as an entry point.
    void join(NodeRef entryPoint);

    // Notifies another node that this node should be considered its predecessor.
    void notify(NodeRef potentialPredecessor);

    // Leaves the Chord ring, transferring keys and updating neighboring nodes.
    void leave();
}
