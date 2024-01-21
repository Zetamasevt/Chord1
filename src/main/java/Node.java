import java.util.ArrayList;

public class Node {


    // https://onecompiler.com/javascript/3zzek2bdu


    public int id = 0;
    public int m = 0;
    private int twoPowerM = 0;
    public ArrayList<Finger> fingers;
    public Node successor;
    public Node predecessor;

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
        successor = this;
        predecessor = this;
        calculateFingerTable();
    }

    /*
    2. To fill the own finger table, the new node simply queries for the ideal adresse the finger pointer
    should be pointing to (n + 2^(i-1)) modulus 2^m and stores the resulting node addresses.
    3. To update the finger pointers of other nodes, the corresponding nodes have to be located. The following
    pseudocode fragment does the trick (remember, m is the number of finger table entries):
    for i = 1 to m
     // Find last node p whose i-th finger might be n
     p = lookup_predecessor(n-2^(i-1)) // ^ is the exponentiation operator
     // update fingers counter-clockwise as long as necessary
     while p.getFinger(i) > n
     // update i-th finger
     p.setFinger(i, n)
     p = p.getPredecessor()
     endwhile
    endfor
     */

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

        // Look for successor and predecessor
        nPrime.calculateFingerTable();
        successor = network.network.get(nPrime.fingers.get(nPrime.findFingerWhoContainsN(id)).node).node;
        while (successor.id <= id && successor.id != 1){
            System.out.println("Checking node " + successor.id);
            nPrime = successor;
            nPrime.calculateFingerTable();
            successor = network.network.get(nPrime.fingers.get(nPrime.findFingerWhoContainsN(id)).node).node;
        }
        predecessor = successor.predecessor;
        successor.predecessor = this;
        predecessor.successor = this;

        /* TODO: This does not work...
        calculateFingerTableSimple();

        for (int i = 0; i < m; i++) {
            int idToLookup = (int) (id - Math.pow(2, (i)));
            Node p = lookupPredecessor(idToLookup, nPrime);
            System.out.println(p.id);
            while (p.fingers.get(i).node > id) {
                p.fingers.get(i).node = id;
                p = p.predecessor;
            }
        }
         */

        network.network.get(id).node = this;
        network.network.get(id).isNode = true;
        System.out.println("New node registered at identifier " + id);
    }

    public int findFingerWhoContainsN(int n){
        //System.out.println("Looking in " + this.id + " for the finger containing " + n);
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

    public Node connectToNode(int n){
        if (this.id == n){
            System.out.println("Found node " + this.id);
            return this;
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
        System.out.println("Successor: " + successorFound.id);
        if (successorFound.id <= n) return successorFound.connectToNode(n);
        System.err.println("No node found");
        return null;
    }

    public void calculateFingerTable() {
        fingers = new ArrayList<>();
        for (int k = 0; k<m; k++){
            Finger finger = new Finger();
            fingers.add(finger);
            finger.start = (int) ((id + Math.pow(2,(k))) % twoPowerM);
            if (k>0 && k<m){
                fingers.get(k-1).end = finger.start;
            }
            for (int i = 0; i< twoPowerM; i++){
                int idToCheck = (finger.start + i) % twoPowerM;
                if (network.network.get(idToCheck).isNode) {
                    finger.node = idToCheck;
                    break;
                };
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
            if (k>0 && k<m){
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
        System.out.println("Fingertable of node " + id);
        int i = 1;
        for (Finger f:fingers){
            System.out.print("Finger " + i + ": ");
            System.out.print(".start: " + f.start + "; ");
            System.out.print(".interval: [" + f.start + "," + f.end + ")"  + "; ");
            System.out.println(".node: " + f.node);
            i += 1;
        }
    }

    public Node lookupPredecessor(int id, Node n){
        Node predecessor = n.predecessor;
        while (predecessor.id > id && id != 1){
            n = predecessor;
            predecessor = n.predecessor;
        }
        return predecessor;
    }
}
