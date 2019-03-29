/**
 * Created by JoseMiguel on 3/25/2019.
 * Some parts are taken from http://www.jcgonzalez.com/java-socket-mini-server-http-ejemplo
 */
import java.net.ServerSocket; //Socket util

public class Connection {
    private static final int PORT = 5000; //Default port

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);

                while (true) { //For every request on PORT
                    new Server(server.accept());
                }
            } catch (Exception e) {
            System.err.println(e);
        }
    }
}

