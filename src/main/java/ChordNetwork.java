import java.util.ArrayList;

public class ChordNetwork {

    public ArrayList<Identifier> network;
    public int m;

    public ChordNetwork(int m){
        this.m = m;
        network = new ArrayList<>();
        for (int i = 0; i < Math.pow(2, m); i++){
            Identifier identifier = new Identifier(i);
            network.add(identifier);
        }
        System.out.println("New Chord Network created. ");
    }

    public void printChordNetwork(){
        for (Identifier i:network){
            //System.out.println(i.key + ": ");
            if (i.isNode){
                System.out.println("Pre: " + i.node.predecessor + "; Succ: " + i.node.successor);
                i.node.calculateFingerTable();
                i.node.printFingerTableFull();
            }
            System.out.println();
        }
    }
}
