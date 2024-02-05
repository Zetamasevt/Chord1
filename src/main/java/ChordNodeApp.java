import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class ChordNodeApp extends Application {

    private static ChordNodeService chordNodeService;

    final static int SERVER_PORT = 9000;

    final static String LOCALHOST = "http://localhost:";
    final static String SERVER_PATH_PREFIX = "/api";

    private Set<Object> singletons = new HashSet<Object>();

    public ChordNodeApp() {
        singletons.add(chordNodeService);
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    public static void main(String args[]) {

        if (args.length < 1) {
            System.err.println("No id given...");
            System.exit(1);
        }

        // Create chord node
        int id = Integer.parseInt(args[0]);
        chordNodeService = new ChordNodeService();
        chordNodeService.setId(id);

        if (id == 1){
            chordNodeService.setM(5);
            chordNodeService.setTwoPowerM((int)Math.pow(2, chordNodeService.getM()));
            chordNodeService.calculateFingerTable();
        }
        else chordNodeService.join(1);

        System.out.println("Hello World! I am a new node. My id is " + chordNodeService.getId());

        // Starting server
        Thread serverThread = new Thread(() -> {
            try {
                startServer(SERVER_PORT + id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();


    }

    private static void startServer(int port){
        System.out.println("Starting Server at port " + (port));
        Server server = new Server(port);
        final ServletContextHandler context = new ServletContextHandler(server, "/");
        final ServletHolder restEasyServlet = new ServletHolder(new HttpServletDispatcher());
        restEasyServlet.setInitParameter("resteasy.servlet.mapping.prefix", SERVER_PATH_PREFIX);
        restEasyServlet.setInitParameter("javax.ws.rs.Application", ChordNodeApp.class.getCanonicalName());
        context.addServlet(restEasyServlet,  SERVER_PATH_PREFIX + "/*");
        final ServletHolder defaultServlet = new ServletHolder(new DefaultServlet());
        context.addServlet(defaultServlet, "/");
        server.setHandler(context);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
