import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Path("/node")
public class ChordNodeService {

    private int id = -1;
    private int m = 0;
    private int twoPowerM = 0;
    private ArrayList<Finger> fingers;

    private HashMap<Integer, String> hashMap = new HashMap<>();
    private int successor = -1;
    private int predecessor = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @GET
    @Path("/getM")
    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getTwoPowerM() {
        return twoPowerM;
    }

    public void setTwoPowerM(int twoPowerM) {
        this.twoPowerM = twoPowerM;
    }


    @GET
    @Path("/findSuccessor")
    @Produces(MediaType.APPLICATION_JSON)
    public NodeRef findSuccessor(String identifier) {
        return null;
    }


    @GET
    @Path("/getSuccessor")
    @Produces(MediaType.APPLICATION_JSON)
    public int getSuccessor() {
        return successor;
    }

    @PUT
    @Path("/setSuccessor")
    @Consumes(MediaType.APPLICATION_JSON)
    public void setSuccessor(int successor) {
        this.successor = successor;
    }

    @GET
    @Path("/getPredecessor")
    @Produces(MediaType.APPLICATION_JSON)
    public int getPredecessor() {
        return predecessor;
    }

    @PUT
    @Path("/setPredecessor")
    @Consumes(MediaType.APPLICATION_JSON)
    public void setPredecessor(int predecessor) { this.predecessor=predecessor; }


    @POST
    @Path("/notify")
    @Consumes(MediaType.APPLICATION_JSON)
    public void notify(int potentialPredecessor) {
    }


    @DELETE
    @Path("/leave")
    public void leave() {

    }


    @GET
    @Path("/checkIfNode")
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean checkIfNode(@QueryParam("id") int id) {
        boolean isNode = false;
        //System.out.println("Trying to reach node " + id);
        int port = ChordNodeApp.SERVER_PORT + id;
        String nodeAddress = ChordNodeApp.LOCALHOST + port + ChordNodeApp.SERVER_PATH_PREFIX;
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        clientBuilder.connectTimeout(1, TimeUnit.SECONDS);
        clientBuilder.readTimeout(1, TimeUnit.SECONDS);
        Client client = clientBuilder.build();
        WebTarget target = client.target(nodeAddress);
        ResteasyWebTarget retarget = (ResteasyWebTarget) target;
        ChordNodeInterface node = retarget.proxy(ChordNodeInterface.class);
        try{
            if(node.check() == 1) isNode = true;
            client.close();
            return isNode;
        }
        catch (Exception e) {
            client.close();
            return false;
        }
    }

    @GET
    @Path("/check")
    public int check() {
        return 1;
    }

    @GET
    @Path("/sendMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendMessage(@QueryParam("id") int id, @QueryParam("message") String message){
        int n = id;
        if (this.id == n){
            System.out.println(this.id + ": Received message: " + message);
            return;
        }
        System.out.println("Sending message to " + id);
        calculateFingerTable();
        int fingerFound = -1;
        while (fingerFound == -1){
            for (Finger f:fingers) {
                int fi = f.start;
                while (fi != f.end) {
                    if (n == fi) {
                        fingerFound = f.node;
                    }
                    fi = (fi + 1) % twoPowerM;
                }
            }
        }
        if (fingerFound <= n) {
            int port = ChordNodeApp.SERVER_PORT + fingerFound;
            String nodeAddress = ChordNodeApp.LOCALHOST + port + ChordNodeApp.SERVER_PATH_PREFIX;
            ClientBuilder clientBuilder = ClientBuilder.newBuilder();
            Client client = clientBuilder.build();
            WebTarget target = client.target(nodeAddress);
            ResteasyWebTarget retarget = (ResteasyWebTarget) target;
            ChordNodeInterface node = retarget.proxy(ChordNodeInterface.class);
            node.sendMessage(id, message);
            client.close();
        }
    };

    @GET
    @Path("/calculateFingerTable")
    public synchronized void calculateFingerTable() {
        System.out.println("Updating Fingers for node " + id);
        fingers = new ArrayList<>();
        for (int k = 0; k<m; k++){
            Finger finger = new Finger();
            fingers.add(finger);
            finger.start = (int) ((id + Math.pow(2,(k))) % twoPowerM);
            if (k>0){
                fingers.get(k-1).end = finger.start;
            }
            for (int i = 0; i< twoPowerM; i++){
                int idToCheck = (finger.start + i) % twoPowerM;
                if (checkIfNode(idToCheck)) {
                    finger.node = idToCheck;
                    break;
                }
            }
        }
        fingers.get(m-1).end = id;
        this.successor = fingers.get(0).node;
        //printFingerTableFull();
    }

    public void printFingerTableFull(){
        System.out.println("Finger-table of node " + id);
        int i = 1;
        for (Finger f:fingers){
            System.out.print("Finger " + i + ": ");
            System.out.print(".start: " + f.start + "; ");
            System.out.print(".interval: [" + f.start + "," + f.end + ")"  + "; ");
            System.out.println(".node: " + f.node);
            i += 1;
        }
    }

    private ArrayList<Integer> createInterval(int start, int end, int ringSize){
        if (start > ringSize || end > ringSize) return null; // Wrong values
        ArrayList<Integer> interval = new ArrayList<>();
        interval.add(start);
        for (int i = start+1; i%ringSize != end+1; i++){
            interval.add(i%ringSize);
        }
        return interval;
    }

    @GET
    @Path("/findPredecessor")
    public int findPredecessor(@QueryParam("id") int id){
        id = (twoPowerM + id)%twoPowerM; // That is needed because simple algorithm can result in negative values (going counterclockwise in the ring)
        int nPrime = this.id;
        ArrayList<Integer> interval = createInterval(nPrime+1, successor, twoPowerM);
        System.out.println(interval);
        while (!interval.contains(id)){
            nPrime = askClosestPrecedingFinger(id, nPrime);
            System.out.println("nPrime is " + nPrime);
            interval = createInterval(nPrime+1, askSuccessor(nPrime), twoPowerM);
            System.out.println(interval);
        }
        return nPrime;
    }

    @GET
    @Path("/closestPrecedingFinger")
    public int closestPrecedingFinger(@QueryParam("id") int id){
        calculateFingerTable();
        ArrayList<Integer> interval = createInterval(this.id+1, id-1, twoPowerM);
        for (int i = m-1; i >= 0; i--){
            if (interval.contains(fingers.get(i).node)) return fingers.get(i).node;
        }
        return this.id;
    }

    private ChordNodeInterface connectToNode(int node){
        int port = ChordNodeApp.SERVER_PORT + node;
        String nodeAddress = ChordNodeApp.LOCALHOST + port + ChordNodeApp.SERVER_PATH_PREFIX;
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        Client client = clientBuilder.build();
        WebTarget target = client.target(nodeAddress);
        ResteasyWebTarget retarget = (ResteasyWebTarget) target;
        ChordNodeInterface proxy = retarget.proxy(ChordNodeInterface.class);
        return proxy;
    }

    @GET
    @Path("/askClosestPrecedingFinger")
    public int askClosestPrecedingFinger(@QueryParam("id") int id, @QueryParam("node") int node){
        System.out.println("Asking node " + node + " for the closest finger preceding " + id);
        int port = ChordNodeApp.SERVER_PORT + node;
        String nodeAddress = ChordNodeApp.LOCALHOST + port + ChordNodeApp.SERVER_PATH_PREFIX;
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        Client client = clientBuilder.build();
        WebTarget target = client.target(nodeAddress);
        ResteasyWebTarget retarget = (ResteasyWebTarget) target;
        ChordNodeInterface proxy = retarget.proxy(ChordNodeInterface.class);
        int cfp = proxy.closestPrecedingFinger(id);
        client.close();
        System.out.println("Answer was " + cfp);
        return cfp;
    };

    @GET
    @Path("/askSuccessor")
    public int askSuccessor(@QueryParam("node") int node){
        int n = node;
        if (n == this.id) {
            calculateFingerTable();
            this.successor = fingers.get(0).node;
            return this.successor;
        }
        System.out.println("Asking node " + node + " for its successor...");
        int port = ChordNodeApp.SERVER_PORT + node;
        String nodeAddress = ChordNodeApp.LOCALHOST + port + ChordNodeApp.SERVER_PATH_PREFIX;
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        Client client = clientBuilder.build();
        WebTarget target = client.target(nodeAddress);
        ResteasyWebTarget retarget = (ResteasyWebTarget) target;
        ChordNodeInterface proxy = retarget.proxy(ChordNodeInterface.class);
        int cfp = proxy.askSuccessor(n);
        client.close();
        System.out.println("Answer was " + cfp);
        return cfp;
    };

    @GET
    @Path("/printNode")
    public String printNode(){
        String nodeInfo = "Node Info for node " + id + "<br>";

        nodeInfo += ("Finger-table:" + "<br>");
        if (fingers == null) calculateFingerTable();
        int i = 1;
        for (Finger f:fingers){
            nodeInfo += ("Finger " + i + ": ");
            nodeInfo += (".start: " + f.start + "; ");
            nodeInfo += (".interval: [" + f.start + "," + f.end + ")"  + "; ");
            nodeInfo += (".node: " + f.node + "<br>");
            i += 1;
        }

        if (successor == -1) successor = fingers.get(0).node;
        if (predecessor == -1) predecessor = findPredecessor(this.id);
        nodeInfo += ("Predecessor: " + predecessor + "<br>");
        nodeInfo += ("Successor: " + successor + "<br>");

        nodeInfo += ("HashMap: " + "<br>");
        for (HashMap.Entry<Integer, String> entry : hashMap.entrySet()) {
            nodeInfo += ("Key: " + entry.getKey() + ", Value: " + entry.getValue() + "<br>");
        }
        return nodeInfo;
    };

    public int hashValue(String string){
        int hash = Math.abs((string.hashCode())%twoPowerM);
        return hash;
    }

    @GET
    @Path("/add")
    public int add(@QueryParam("value") String value){
        System.out.println("We want to store: " + value);
        int hash = hashValue(value);
        System.out.println("The key is: " + hash);
        int nResp = findPredecessor(hash);
        System.out.println("We find the predecessor: " + nResp);
        nResp = askSuccessor(nResp);
        System.out.println("We find the successor of that: " + nResp + " and we store the value there...");
        askToAdd(nResp, value);
        return hash;
    }

    public void store(int key, String value){
        hashMap.put(key, value);
        System.out.println(this.id + " has stored " + value + " with key " + key);
    }

    @GET
    @Path("/askToAdd")
    @Consumes(MediaType.APPLICATION_JSON)
    public void askToAdd(@QueryParam("id") int id, @QueryParam("value") String value){
        int n = id;
        if (this.id == n){
            System.out.println(this.id + ": Adding: " + value);
            int hash = hashValue(value);
            store(hash, value);
            return;
        }
        System.out.println("Sending add request to " + id);
        calculateFingerTable();
        int fingerFound = -1;
        while (fingerFound == -1){
            for (Finger f:fingers) {
                int fi = f.start;
                while (fi != f.end) {
                    if (n == fi) {
                        fingerFound = f.node;
                    }
                    fi = (fi + 1) % twoPowerM;
                }
            }
        }
        if (fingerFound <= n) {
            int port = ChordNodeApp.SERVER_PORT + fingerFound;
            String nodeAddress = ChordNodeApp.LOCALHOST + port + ChordNodeApp.SERVER_PATH_PREFIX;
            ClientBuilder clientBuilder = ClientBuilder.newBuilder();
            Client client = clientBuilder.build();
            WebTarget target = client.target(nodeAddress);
            ResteasyWebTarget retarget = (ResteasyWebTarget) target;
            ChordNodeInterface node = retarget.proxy(ChordNodeInterface.class);
            node.askToAdd(id, value);
            client.close();
        }
    };

    @GET
    @Path("/get")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@QueryParam("key") int key){
        System.out.println("We look for the value of: " + key + " starting from node " + this.id);
        int nResp = findPredecessor(key);
        System.out.println("We find the predecessor: " + nResp);
        nResp = askSuccessor(nResp);
        System.out.println("We find the successor of that: " + nResp + " and we look for the key there...");
        if (this.id == nResp) {
            //Todo: implement check and error message if key is not stored
            return hashMap.get(key);
        }
        else {
            int port = ChordNodeApp.SERVER_PORT + nResp;
            String nodeAddress = ChordNodeApp.LOCALHOST + port + ChordNodeApp.SERVER_PATH_PREFIX;
            ClientBuilder clientBuilder = ClientBuilder.newBuilder();
            Client client = clientBuilder.build();
            WebTarget target = client.target(nodeAddress);
            ResteasyWebTarget retarget = (ResteasyWebTarget) target;
            ChordNodeInterface node = retarget.proxy(ChordNodeInterface.class);
            String value = node.get(key);
            client.close();
            return value;
            }
    }

    @GET
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    public void remove(@QueryParam("key") int key){
        System.out.println("We look for the value of: " + key + " starting from node " + this.id);
        int nResp = findPredecessor(key);
        System.out.println("We find the predecessor: " + nResp);
        nResp = askSuccessor(nResp);
        System.out.println("We find the successor of that: " + nResp + " and we look for the key there...");
        if (this.id == nResp) {
            //Todo: implement check and error message if key is not stored
            hashMap.remove(key);
            System.out.println("The key " + key + " has succesfully been removed in node " + this.id);
        }
        else {
            int port = ChordNodeApp.SERVER_PORT + nResp;
            String nodeAddress = ChordNodeApp.LOCALHOST + port + ChordNodeApp.SERVER_PATH_PREFIX;
            ClientBuilder clientBuilder = ClientBuilder.newBuilder();
            Client client = clientBuilder.build();
            WebTarget target = client.target(nodeAddress);
            ResteasyWebTarget retarget = (ResteasyWebTarget) target;
            ChordNodeInterface node = retarget.proxy(ChordNodeInterface.class);
            node.remove(key);
            System.out.println("The key " + key + " has succesfully been removed in node " + nResp);
            client.close();
        }
    }

    private void calculateFingerTableSimple() {
        fingers = new ArrayList<>();
        for (int k = 0; k<m; k++){
            Finger finger = new Finger();
            fingers.add(finger);
            finger.start = (int) ((id + Math.pow(2,(k))) % twoPowerM);
            finger.node = finger.start;
            if (k>0){
                fingers.get(k-1).end = finger.start;
            }
        }
        fingers.get(m-1).end = id;
    }

    @GET
    @Path("/getFingerNode")
    @Consumes(MediaType.APPLICATION_JSON)
    public int getFingerNode(@QueryParam("fingerIndex") int fingerIndex){
        return this.fingers.get(fingerIndex).node;
    }

    @GET
    @Path("/setFingerNode")
    @Consumes(MediaType.APPLICATION_JSON)
    public void setFingerNode(@QueryParam("fingerIndex") int fingerIndex, @QueryParam("node") int node){
        fingers.get(fingerIndex).node = node;
    }

    @POST
    @Path("/join")
    @Consumes(MediaType.APPLICATION_JSON)
    public void join(int node){
        System.out.println("New node joins: " + this.id);
        String nodeAddress = ChordNodeApp.LOCALHOST + (ChordNodeApp.SERVER_PORT + node) + ChordNodeApp.SERVER_PATH_PREFIX;
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        Client client = clientBuilder.build();
        WebTarget target = client.target(nodeAddress);
        ResteasyWebTarget retarget = (ResteasyWebTarget) target;
        ChordNodeInterface nPrime = retarget.proxy(ChordNodeInterface.class);
        System.out.println("We have successfully connected to the node " + node);

        this.m = nPrime.getM();
        this.twoPowerM = (int)Math.pow(2, m);

        //Simplified algorithm from ES4
        calculateFingerTableSimple();

        for (int i = 1; i <= m; i++) {
            int idToLookup = (int) (id - Math.pow(2, (i-1)));
            String nodeAddressP = ChordNodeApp.LOCALHOST + (ChordNodeApp.SERVER_PORT + nPrime.findPredecessor(idToLookup)) + ChordNodeApp.SERVER_PATH_PREFIX;
            ClientBuilder clientBuilderP = ClientBuilder.newBuilder();
            Client clientP = clientBuilderP.build();
            WebTarget targetP = clientP.target(nodeAddressP);
            ResteasyWebTarget retargetP = (ResteasyWebTarget) targetP;
            ChordNodeInterface p = retargetP.proxy(ChordNodeInterface.class);
            while (p.getFingerNode(i-1) > id) {
                p.setFingerNode(i-1, id);
                nodeAddressP = ChordNodeApp.LOCALHOST + (ChordNodeApp.SERVER_PORT + p.getPredecessor()) + ChordNodeApp.SERVER_PATH_PREFIX;
                clientBuilderP = ClientBuilder.newBuilder();
                clientP = clientBuilderP.build();
                targetP = clientP.target(nodeAddressP);
                retargetP = (ResteasyWebTarget) targetP;
                p = retargetP.proxy(ChordNodeInterface.class);
            }
            clientP.close();
        }

        // Find the predecessor of the new node
        nPrime.calculateFingerTable();
        predecessor = nPrime.findPredecessor(id);

        // Find the successor of the new node
        calculateFingerTable();

        // Inform the predecessor that it must update its successor
        // Inform the successor that it must update its predecessor

        System.out.println("New node registered at identifier " + id + "; Pre: " + predecessor + "; Succ: " + successor);
    }
}
