import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Node {
    // For checking...
    // https://onecompiler.com/javascript/3zzek2bdu

    public int id;
    public int m = 0;
    private int twoPowerM = 0;
    public ArrayList<Finger> fingers;
    public int successor;
    public int predecessor;

    private HashMap<Integer, String> hashMap = new HashMap<>();

    public void setNetwork(ChordNetwork network) {
        this.network = network;
    }

    private ChordNetwork network;

    public Node (int id) {
        this.id = id;
    }

    public void joinNetworkAsFirstNode (ChordNetwork network) {
        m = network.m;
        twoPowerM = (int)Math.pow(2, m);
        setNetwork(network);
        network.network.get(id).isNode = true;
        network.network.get(id).node = this;
        successor = this.id;
        predecessor = this.id;
        calculateFingerTable();
    }

    public void joinNetwork(Node nPrime){
        network = nPrime.network;
        m = nPrime.m;
        twoPowerM = (int)Math.pow(2, m);
        boolean positionFound = false;
        int attempts = 0;
        while (!positionFound) {
            Identifier position = network.network.get(id);
            if (position.isNode) {
                id = (id+1)%twoPowerM;
                attempts++;
            }
            else positionFound = true;
            // TODO: What happens when no free identifiers are available?
            if (attempts == twoPowerM) {
                System.err.println("Network is full.");
                System.exit(1);
            }
        }

        //Simplified algorithm from ES4
        //System.out.println("Calculating finger-tables for " + id);
        calculateFingerTableSimple();
        for (int i = 1; i <= m; i++) {
            //System.out.println("Finger: " + i);
            int idToLookup = (int) (id - Math.pow(2, (i-1)));
            //System.out.println("Id to look up: " + idToLookup);
            Node p = network.network.get(nPrime.findPredecessor(idToLookup)).node;
            //System.out.println("Predecessor of Id to look up: " + p.id);
            while (p.fingers.get(i-1).node > id) {
                //System.out.print(p.fingers.get(i-1).node);
                //System.out.println(" is bigger than " + id);
                //System.out.println("Updating finger " + i);
                p.fingers.get(i-1).node = id;
                p = network.network.get(p.predecessor).node;
                //System.out.println("The new predecessor is " + p.id);
            }
        }

        nPrime.calculateFingerTable();

        predecessor = nPrime.findPredecessor(id);
        successor = network.network.get(predecessor).node.successor;
        network.network.get(successor).node.predecessor = this.id;
        network.network.get(predecessor).node.successor = this.id;

        network.network.get(id).node = this;
        network.network.get(id).isNode = true;
        System.out.println("New node registered at identifier " + id + "; Pre: " + predecessor + "; Succ: " + successor);
    }

    public int findFingerWhoContainsN(int n){
        calculateFingerTable();
        int fingerIndex = 0;
        for (Finger f:fingers) {
            int fi = f.start;
            while (fi != f.end) {
                if (n == fi) {
                    return fingerIndex;
                }
                fi = (fi + 1) % twoPowerM;
            }
            fingerIndex++;
        }
        return -1;
    }

    public boolean connectToNode(int n, String message){
        if (this.id == n){
            System.out.println(this.id + ": Received message: " + message);
            return true;
        }
        calculateFingerTable();
        Node successorFound = null;
        while (successorFound == null){
            for (Finger f:fingers) {
                int fi = f.start;
                while (fi != f.end) {
                    if (n == fi) {
                        successorFound = network.network.get(f.node).node;
                    }
                    fi = (fi + 1) % twoPowerM;
                }
            }
        }
        if (successorFound.id <= n) return successorFound.connectToNode(n, message);
        System.err.println("No node found");
        return false;
    }

    public void calculateFingerTable() {
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
                if (network.network.get(idToCheck).isNode) {
                    finger.node = idToCheck;
                    break;
                }
            }
        }
        fingers.get(m-1).end = id;
    }

    public void calculateFingerTableSimple() {
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

    public void printFingerTable(){
        System.out.print(id + ": [ ");
        for (Finger f:fingers){
            System.out.print(f.node  + " ");
        }
        System.out.println("]");
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
    public int closestFingerPrecedingId(int id){
        this.calculateFingerTable();
        ArrayList<Integer> interval = createInterval(this.id+1, id-1, twoPowerM);
        for (int i = m-1; i >= 0; i--){
            //This also seems to work, but I am not sure if this is always correct
            //if (fingers.get(i).node > this.id && fingers.get(i).node < id) return fingers.get(i).node;
            //else if (id < this.id && fingers.get(i).node < id) return fingers.get(i).node;
            if (interval.contains(fingers.get(i).node)) return fingers.get(i).node;
        }
        return this.id;
    }


    public int findPredecessor(int id){
        id = (twoPowerM + id)%twoPowerM; // That is needed because simple algorithm can result in negative values (going counterclockwise in the ring)
        Node nPrime = this;
        ArrayList<Integer> interval = createInterval(nPrime.id+1, nPrime.successor, twoPowerM);
        while (!interval.contains(id)){
            nPrime = network.network.get(nPrime.closestFingerPrecedingId(id)).node;
            interval = createInterval(nPrime.id+1, nPrime.successor, twoPowerM);
        }
        return nPrime.id;
    }

    public int findSuccessor(int id){
        Node nPrime = network.network.get(findPredecessor(id)).node;
        return nPrime.successor;
    }

    //KISS for ring intervals. Not very efficient but prevents brains from exploding. ATTENTION: both start and end is inclusive here...
    public ArrayList<Integer> createInterval(int start, int end, int ringSize){
        if (start > ringSize || end > ringSize) return null; //Wrong values
        ArrayList<Integer> interval = new ArrayList<>();
        interval.add(start);
        for (int i = start+1; i%ringSize != end+1; i++){
            interval.add(i%ringSize);
        }
        return interval;
    }

    public void periodicalUpdate(){
        //Update fingers...
        calculateFingerTable();
        //Check successor...
        checkSuccessor();
    }

    public boolean checkSuccessor(){
        if (!connectToNode(successor, "Test")){
            System.out.println("Node " + successor + " can not be reached!");
            for (int i = 1; i<m; i++){
                if (fingers.get(i).node != successor && network.network.get(fingers.get(i).node).node.predecessor == successor){
                    successor = fingers.get(i).node;
                    network.network.get(fingers.get(i).node).node.predecessor = this.id;
                    System.out.println("New successor: " + successor);
                    return true;
                }
            }
        }
        return false;
    }


    //DHT-Logic: add, get and remove
    public int hashValue(String string){
        int hash = Math.abs((string.hashCode())%twoPowerM);
        return hash;
    }

    public int add(String string){
        System.out.println("We want to store: " + string);
        int hash = hashValue(string);
        System.out.println("The key is: " + hash);
        Node nResp = network.network.get(findPredecessor(hash)).node;
        //System.out.println("We find the predecessor: " + nResp.id);
        nResp = network.network.get(nResp.successor).node;
        //System.out.println("We find the successor of that: " + nResp.id);
        nResp.store(hash, string);
        return hash;
    }

    public void store(int key, String value){
        hashMap.put(key, value);
        System.out.println(this.id + " has stored " + value + " with key " + key);
    }

    public String get(int key){
        System.out.println("We look for the value of: " + key + " in node " + this.id);
        Node nResp = network.network.get(findPredecessor(key)).node;
        nResp = network.network.get(nResp.successor).node;
        if (this == nResp) {
            //Todo: implement check and error message if key is not stored
            return hashMap.get(key);
        }
        else return nResp.get(key);
    }

    public void remove(int key){
        System.out.println("We look for the key " + key + " to remove in node " + this.id);
        Node nResp = network.network.get(findPredecessor(key)).node;
        nResp = network.network.get(nResp.successor).node;
        if (this == nResp) {
            //Todo: implement check and error message if key is not stored
            hashMap.remove(key);
            System.out.println("The key " + key + " has succesfully been removed in node " + this.id);
        }
        else nResp.remove(key);
    }

    public Collection<String> values(){
        return hashMap.values();
    }
}
