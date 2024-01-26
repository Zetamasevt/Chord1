import java.util.Objects;

public class NodeRef {
    private final String nodeId;
    private final String restUri;

    public NodeRef(String nodeId, String restUri) {
        if (nodeId == null || nodeId.isEmpty()) {
            throw new IllegalArgumentException("Node ID cannot be null or empty");
        }
        if (restUri == null || restUri.isEmpty()) {
            throw new IllegalArgumentException("REST URI cannot be null or empty");
        }

        this.nodeId = nodeId;
        this.restUri = restUri;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getRestUri() {
        return restUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeRef nodeRef = (NodeRef) o;
        return nodeId.equals(nodeRef.nodeId) && restUri.equals(nodeRef.restUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, restUri);
    }

    @Override
    public String toString() {
        return "NodeRef{" +
                "nodeId='" + nodeId + '\'' +
                ", restUri='" + restUri + '\'' +
                '}';
    }
}
