/**
 * Created by JoseMiguel on 3/25/2019.
 * Some parts are taken from http://www.jcgonzalez.com/java-socket-mini-server-http-ejemplo
 */


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.lang.Object;
import java.util.Hashtable;

public class Server extends Thread {
    private Socket socket;
    private static final int METHOD = 0;
    private static final int URL = 1;
    private static final int REFERER = 0;
    private static final int POST = 1;
    private static final String DEFAULT = "/";

    public String[] media = {".css",".csv", ".doc", ".docx", ".exe", ".gif", ".html",".ico", ".jar", ".java", ".jpeg", ".jpe", ".jpg", ".js", ".latex", ".mp3", ".mp4", ".png", ".rgb", ".shtml", ".xhtml", "none"};
    public String[] mimeType = {"text/css", "text/csv", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/x-msdos-program", "image/gif", "text/html", "image/png", "application/java-archive", "text/x-java", "image/jpeg", "image/jpeg", "image/jpeg", "application/x-javascript", "application/x-latex", "audio/mpeg", "video/mp4", "image/png", "image/x-rgb", "text/html", "application/xhtml+xml", "none"};
    public Hashtable<String, String> mimeTypesVerify = new Hashtable<String, String>();

    private View view;

    Server (View view, Socket socket) {
        this.socket = socket;
        this.view = view;
        this.fillHash();// fill the hash with the mimetypes
        this.start(); //Runs the thread
    }

    /**
     * Fills the mimetypes hash
     */
    public void fillHash(){
        int arrayLength = this.media.length;

        for (int iteradorArrays = 0; iteradorArrays < arrayLength ; iteradorArrays++ ){
            String key = this.media[iteradorArrays];
            String value = this.mimeType [iteradorArrays];

            this.mimeTypesVerify.put(key, value);
        }
    }

    /**
     * Extract the extension from file
     * @param file String containing resource asked by the browser
     * @return String with the extension, if it doesn't have, returns none
     */
    public String extractExtension(String file){
        String division[] = file.split("\\.");
        String extension = "406";
        int length = division.length;
        if (length > 1){
            extension ="."+ division[1];
        }
        if(file.compareTo("/")==0){
            extension = "none";
        }
        return extension;

    }


    /**
     * This method uses an object of class view to show the asked resources
     * @param mimeType String containing the resource mimetype
     * @param out Printwriter used to show the resources on browser, text only
     * @param binaryOut Dataoutputstream to show resources, can show images
     * @param in
     * @param url String containing what is asked by browser
     */
    public void GET(String mimeType, PrintWriter out, DataOutputStream binaryOut, BufferedReader in, String url){

        if(mimeType.compareTo("none") == 0) {
           view.sendHTML(out, binaryOut, "index.html", "text");
        } else {
            System.out.println("Other GET");
            view.sendHTML(out, binaryOut, url,mimeType);
        }

    }

    /**
     * Runs the main function that controls the server, managing the cases
     */
    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream(); //Gets request from browser
            PrintWriter out = new PrintWriter(socket.getOutputStream()); //TODO: I think we don't need it :o Creates a new PrintWriter, without automatic line flushing, from an existing OutputStream. This convenience constructor creates the necessary intermediate OutputStreamWriter, which will convert characters into bytes using the default character encoding.
            BufferedReader in = new BufferedReader(new InputStreamReader(is)); //Saves input from browser on buffer
            DataOutputStream binaryOut = new DataOutputStream(socket.getOutputStream());
            String line; //Line to be read
            line = in.readLine(); //Reads first line
            System.out.println("HTTP-HEADER: " + line);

            //Header variables
            String method[] = line.split(" "); //Method GET, POST, HEAD
            String file = method[1];
            boolean havePost = false;
            if(method[METHOD].compareTo("POST") == 0) {
                havePost = true;
            }

            String info[] = getInfo(in, havePost); // Get referer and post information
            String extension = extractExtension(file);
            boolean exist = true;

            System.out.println("Extension: " + extension+ ".");
            boolean mediaSupported = mimeTypesVerify.containsKey(extension);
            boolean hasExtension = true;
            if(extension.compareTo("406")==0){
                hasExtension = false;
            }
            if(file.compareTo(DEFAULT)!=0){
                exist = this.fileExist(file);// if is not default directory, check if file exist
            }

            /*//Prueba de 404
            if(true) {
                view.printNotFound(out);
                out.close();
                socket.close();
            }*/


            if (method[METHOD].compareTo("GET") == 0 || method[0].compareTo("POST") == 0 || method[0].compareTo("HEAD") == 0) {
                    if (method[0].compareTo("POST") == 0) {/************* POST *************/
                        view.writeInLog("POST", info[REFERER], method[URL], info[POST]); //Writes the successful POST
                        this.GET(mimeTypesVerify.get(extension),out, binaryOut, in, method[URL]);
                    } else if (mediaSupported && hasExtension ) {
                        if(exist) {
                            System.out.println("----------------------MEDIA SUPPORTED-------------------");
                            if (method[METHOD].compareTo("GET") == 0) {
                                System.out.println("It's a get");
                                this.GET(mimeTypesVerify.get(extension), out, binaryOut, in, method[URL]);
                                view.writeInLog("GET", info[REFERER], method[URL], ""); //Writes the successful GET
                            } else {
                                //view.writeInLog("HEAD", info[REFERER], method[URL], ""); //Writes the successful GET TODO verifY if is writing
                                //TODO It's a head
                                System.out.println("Its a head");
                            }
                        }
                        else{
                            view.printNotFound(out);
                            System.out.println("ERROR 404");
                        }
                    } else {
                        System.out.println("ERROR 406");
                        view.printNoMediaSupported(out);
                    }

            } else {
                System.out.println("ERROR 501");
                view.printNotImplemented(out);
            }

            out.close();
            socket.close();


        } catch (IOException e) {

                System.err.println(e);
                e.printStackTrace();

        }
    }


    //Get info if exist line exists
    private String[] getInfo(BufferedReader in, boolean havePost) {
        String info[] = {"", ""};
        String line = "";
        int contentLength = 0;

        try {
            while ((line = in.readLine()) != null && (line.length() != 0)) {
                System.out.println("HTTP-HEADER: " + line);
                if (line.contains("Referer:")) {
                    info[REFERER] = getReferer(line.substring("Referer:".length()));
                    System.out.println("Has referer " + info[REFERER]);
                }
                if (line.contains("Content-Length:") && havePost) {
                    contentLength = new Integer(line.substring(line.indexOf("Content-Length:") + 16)).intValue();
                }
            }

            if(contentLength > 0) {
                info[POST] = getPost(contentLength, in);
                System.out.println("Has post " + info[POST]);
            }

        } catch (IOException e) {

        }

        return info;
    }

    //Get hostname from url like  http://localhost:8080/
    private String getReferer(String referer) {
        String array[] = referer.split("//");
        array = array[1].split(":");

        return array[0];
    }

    //Get data from post
    private String getPost(Integer postDataI, BufferedReader in) {
        String postData = "";

        try {
            char[] charArray = new char[postDataI];
            in.read(charArray, 0, postDataI);
            postData = new String(charArray);
        } catch (IOException e) {

        }

        return postData;
    }

    private boolean fileExist( String file){
        boolean exist = false;
        File f = new File("server"+file);
        exist = f.exists();

        return exist;
    }
}
