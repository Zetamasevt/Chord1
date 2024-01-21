import java.util.ArrayList;

public class ChordNetwork {

    public ArrayList<Identifier> network;
    public int m;

    public String networkString(){
        String output = "";
        for (Identifier id :network){
            output += id.key + " ";
        }
        return output;
    }

    public ChordNetwork(int m){
        this.m = m;
        network = new ArrayList<>();
        for (int i = 0; i < Math.pow(2, m); i++){
            Identifier identifier = new Identifier(i);
            network.add(identifier);
        }
        System.out.println("New Chord Network created. " + networkString());
    }

    public void printChordNetwork(){
        for (Identifier i:network){
            //System.out.println(i.key + ": ");
            if (i.isNode){
                System.out.println("Pre: " + i.node.predecessor.id + "; Succ: " + i.node.successor.id);
                i.node.calculateFingerTable();
                i.node.printFingerTableFull();
            }
            System.out.println();
        }
    }
}
