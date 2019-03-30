/**
 * Created by JoseMiguel on 3/25/2019.
 * Some parts are taken from http://www.jcgonzalez.com/java-socket-mini-server-http-ejemplo
 */
import java.io.*;
import java.net.Socket;
import java.lang.Object;
import java.util.Hashtable;

public class Server extends Thread {
    private Socket socket;
    private static final int METHOD = 0;
    private static final int RESOURCE = 1;
    public String[] media = {".css",".csv", ".doc", ".docx", ".exe", ".gif", ".html", ".jar", ".java", ".jpeg", ".jpe", ".jpg", ".js", ".latex", ".mp3", ".mp4", ".png", ".rgb", ".shtml", ".xhtml"};
    public String[] mimeType = {"text/css", "text/csv", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/x-msdos-program", "image/gif", "text/html", "application/java-archive", "text/x-java", "image/jpeg", "image/jpeg", "image/jpeg", "application/x-javascript", "application/x-latex", "audio/mpeg", "video/mp4", "image/png", "image/x-rgb", "text/html", "application/xhtml+xml"};
    public Hashtable<String, String> mimeTypesVerify = new Hashtable<String, String>();

    private View view;

    Server (View view, Socket socket) {
        this.socket = socket;
        this.view = view;
        this.fillHash();// fill the hash with the mimetypes
        this.start(); //Runs the thread
    }

    public void fillHash(){
        int arrayLength = this.media.length;

        for (int iteradorArrays = 0; iteradorArrays < arrayLength ; iteradorArrays++ ){
            String key = this.media[iteradorArrays];
            String value = this.mimeType [iteradorArrays];

            this.mimeTypesVerify.put(key, value);
        }
    }

    public String extractExtension(String file){
        String division[] = file.split("\\.");
        String extension = "."+ division[1];
        return extension;

    }

    public void GET(String mimeType, PrintWriter out, BufferedReader in){

        int postDataI = -1;
        String line = " ";
        try {
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

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + mimeType);
            out.println("Server: MINISERVER");
            //TODO printwrite the content of the request
        }catch(IOException e){
            System.err.println(e);
        }


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
            System.out.println(line);
            String extension = extractExtension(method[1]);



            /*//Prueba de 404
            if(true) {
                view.printNotFound(out);
                out.close();
                socket.close();
            }*/

            //TODO: If skeleton
            if (method[METHOD].compareTo("GET") == 0 || method[0].compareTo("POST") == 0 || method[0].compareTo("HEAD") == 0) {
                if(true) { //TODO change condition. Verify if resource exists
                    if (method[0].compareTo("POST") == 0) {
                        //TODO 200 ok and post implementation
                    } else if (mimeTypesVerify.containsKey(extension)) {//TODO Verify myme type
                        out.println("HTTP/1.0 200 OK\r\n");// 200 ok
                        if(method[0].compareTo("GET") == 0) {
                            //this.GET(mimeTypesVerify.get(extension),out,in);
                        } else {
                            //TODO It's a head
                        }
                    } else {
                        //TODO Error 406
                    }
                } else {
                    view.printNotFound(out);
                }
            } else {
                //TODO Error 501
            }

            out.close();
            socket.close();

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
