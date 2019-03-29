/**
 * Created by JoseMiguel on 3/25/2019.
 * Some parts are taken from http://www.jcgonzalez.com/java-socket-mini-server-http-ejemplo
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Server extends Thread {
    private Socket socket;
    private static final int METHOD = 0;
    private static final int RESOURCE = 1;
    private View view;

    Server (Socket socket) {
        this.socket = socket;
        this.view = new View();
        this.start(); //Runs the thread
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream(); //Gets request from browser
            PrintWriter out = new PrintWriter(socket.getOutputStream()); //TODO: I think we don't need it :o Creates a new PrintWriter, without automatic line flushing, from an existing OutputStream. This convenience constructor creates the necessary intermediate OutputStreamWriter, which will convert characters into bytes using the default character encoding.
            BufferedReader in = new BufferedReader(new InputStreamReader(is)); //Saves input from browser on buffer
            String line; //Line to be read
            line = in.readLine(); //Reads first line
            String request_method = line; //TODO Verify if we need it
            String method[] = line.split(" ");

            if(true) {
                view.printNotFound(out);
                out.close();
                socket.close();
            }
/**
            //TODO: If skeleton
            if (method[METHOD].compareTo("GET") == 0 || method[0].compareTo("POST") == 0 || method[0].compareTo("HEAD") == 0) {
                if(method[RESOURCE].compareTo("holi") == 0) { //TODO change condition. Verify if resource exists
                    if (method[0].compareTo("POST") == 0) {
                        //TODO 200 ok and post implementation
                    } else if (true) {//TODO Verify myme type
                        //TODO 200 OK
                        if(method[0].compareTo("GET") == 0) {
                            //TODO
                        } else {
                            //TODO It's a head
                        }
                    } else {
                        //TODO Error 406
                    }
                } else {
                    view.printNotFound(out);
                    //TODO Error 404
                }
            } else {
                //TODO Error 501
            }
*/
            /*
            System.out.println("HTTP-HEADER: " + line);
            line = "";
            // busca post data
            int postDataI = -1;
            while ((line = in.readLine()) != null && (line.length() != 0)) {
                System.out.println("HTTP-HEADER: " + line);
                if (line.indexOf("Content-Length:") > -1) {
                    postDataI = new Integer(
                            line.substring(
                                    line.indexOf("Content-Length:") + 16,
                                    line.length())).intValue();
                }
            }
            String postData = "";
            // lee el post data
            if (postDataI > 0) {
                char[] charArray = new char[postDataI];
                in.read(charArray, 0, postDataI);
                postData = new String(charArray);
            }
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: text/html; charset=utf-8");
            out.println("Server: MINISERVER");
            // este linea en blanco marca el final de los headers de la response
            out.println("");
            // Envía el HTML
            out.println("<H1>Bienvenido al Mini Server</H1>");
            out.println("<H2>Request Method->" + request_method + "</H2>");
            out.println("<H2>Post->" + postData + "</H2>");
            out.println("<form name=\"input\" action=\"form_submited\" method=\"post\">");
            out.println("Usuario: <input type=\"text\" name=\"user\"><input type=\"submit\"></form>");
            out.close();
            socket.close();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
