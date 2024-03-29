public class Main {
    public static void main(String[] args) {
        int m = 5;

        //create Network and place node at position 1
        ChordNetwork myNetwork = new ChordNetwork(m);
        Node n1 = new Node(1);
        n1.joinNetworkAsFirstNode(myNetwork);

        //place additional nodes on the addresses 3, 7, 8, 13, 15, 18, 25 and 27
        Node n3 = new Node(3);
        n3.joinNetwork(n1);

        Node n7 = new Node(7);
        n7.joinNetwork(n1);

        Node n8 = new Node(8);
        n8.joinNetwork(n3);

        Node n13 = new Node(13);
        n13.joinNetwork(n3);

        Node n15 = new Node(15);
        n15.joinNetwork(n1);

        Node n18 = new Node(18);
        n18.joinNetwork(n1);

        Node n25 = new Node(25);
        n25.joinNetwork(n7);

        Node n27 = new Node(27);
        n27.joinNetwork(n13);


        // Test 1: Node positions
        System.out.println(" ");
        System.out.println("Test 1: Node positions");
        int i = 0;
        for (Identifier id : myNetwork.network){
            if (id.isNode) System.out.print(i + " ");
            i += 1;
        }
        System.out.println(" ");

        // Test 2: Finger-tables of 3 and 18
        System.out.println(" ");
        System.out.println("Test 2: Finger-tables of 3 and 18");
        n3.calculateFingerTable();
        n3.printFingerTable();
        n18.calculateFingerTable();
        n18.printFingerTable();

        // Test 3: 25 sends message to 8
        System.out.println(" ");
        System.out.println("Test 3: 25 sends message to 8");
        System.out.println(n25.connectToNode(8, "hello world"));

        // Test 4: node 22 joins through node 1
        System.out.println(" ");
        System.out.println("Test 4: 22 joins through node 1");
        Node n22 = new Node(22);
        n22.joinNetwork(n1);

        // Test 5: node 15 fails
        System.out.println(" ");
        System.out.println("Test 15 fails...");
        myNetwork.network.get(15).isNode = false;
        myNetwork.network.get(15).node = null;
        n13.periodicalUpdate();

        //myNetwork.printChordNetwork();

        // Test 6: DHT Test
        System.out.println(" ");
        System.out.println("Test 6: DHT Tests");
        // We add a few strings
        System.out.println("Test 6.1: Adding...");
        n3.add("Hello World");
        n3.add("Hello World 2");
        n3.add("Hello World 3");
        // We try to find one of the added strings
        System.out.println(" ");
        System.out.println("Test 6.2: Getting...");
        System.out.println(n3.hashValue("Hello World"));
        System.out.println(n3.get(28));
        System.out.println(n3.get(5));
        // Now we want to remove a key
        System.out.println(" ");
        System.out.println("Test 6.3: Removing...");
        System.out.println(n1.values());
        n3.remove(28);
        System.out.println(n1.values());
    }
}
