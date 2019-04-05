import javax.xml.crypto.Data;
import java.io.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by JoseMiguel on 3/25/2019.
 *
 * Accept
 * Content-type
 * Content-length
 * Date
 * Host
 * Referer
 * Server
 */
public class View {
    private BufferedWriter writer; //Writes in log
    private String logFormat;
    //private String rowSeparator;

    View() {
        logFormat = "%-12s |%-26s |%-14s |%-13s |%-50s |%-25s%n";
       // rowSeparator = "---------------------------------------------------------------------------------------------------------------------------";
        //Initialises server log
        try {
            File tempFile = new File("server/log.txt");

            if(!tempFile.exists()) {
                writer = new BufferedWriter(new FileWriter("server/log.txt"));
                writer.write(String.format(logFormat, "Método", "Estampilla de Tiempo", "Servidor", "Refiere", "URL", "Datos"));
                writer.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /** Print on screen the type of error.
    * HTTP/1.1 404 Not Found
    Content-Length: 1635
    Content-Type: text/html
    Server: Microsoft-IIS/6.0
    X-Powered-By: ASP.NET
    Date: Tue, 04 May 2010 22:30:36 GMT
    Connection: close*/
    public void printNotFound(PrintWriter printWriter){
        String stringFile = getStringFile("server/NotFound.html"); //Ask for the file and converts into string

        printWriter.println("HTTP/1.1 404 Not Found");
        printWriter.println("Content-Length: " + stringFile.length());
        printWriter.println("Content-Type: text/html");
        printWriter.println("Server: JavaServer");
        printWriter.println("Date: " + new Date());
        printWriter.println("Connection: close");
        printWriter.println();
        printWriter.println(stringFile); //Content
    }

    public void sendHTML(PrintWriter out, DataOutputStream binaryOut, String resource, String mimeType) {
         String type = this.getType(mimeType);
        if(type.compareTo("text")==0) {
            String stringFile = getStringFile("server/" + resource); //Ask for the file and converts into string
            //Response header
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: "+mimeType+"; charset=utf-8");
            out.println("Content-Length: " + stringFile.length());
            out.println("Server: MINISERVER");
            // este linea en blanco marca el final de los headers de la response
            out.println("");
            out.println(stringFile); //Content
        }
        else if(type.compareTo("image")==0){
           byte[] data = this.getDataFile("server/" + resource);

            try {
                binaryOut.writeBytes("HTTP/1.0 200 OK\r\n");
                binaryOut.writeBytes("Content-Type: "+mimeType+"\r\n");
                binaryOut.writeBytes("Content-Length: " + data.length);
                binaryOut.writeBytes("\r\n\r\n");
                binaryOut.write(data);
                binaryOut.close();
            } catch (IOException e){
                System.err.println(e);
            }

        }


    }

    //Converts file into string
    private String getStringFile(String file) {
        String stringFile = "";

        try {
            InputStream inFile = new FileInputStream(file);
            stringFile = new BufferedReader(new InputStreamReader(inFile))
                    .lines().collect(Collectors.joining("\n"));

        } catch (Exception e) {
            System.err.println(e);
        }

        return stringFile;
    }

    private byte[] getDataFile(String file){
        File imageFile = new File(file);
        byte[] data = new byte[(int) imageFile.length()];
        try {

            FileInputStream fis = new FileInputStream(imageFile);

            fis.read(data);
            fis.close();
        } catch (Exception e){
            System.err.println(e);
        }
        return data;
    }

    //Print in server log.
    public void writeInLog(String method, String referer, String url, String data) {
        try(FileWriter fw = new FileWriter("server/log.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.print(String.format(String.format(logFormat, method, new Timestamp(System.currentTimeMillis()).getTime(), "localhost", referer, url, data)));
            //more code
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    public String getType(String mimeType){
        String division[] = mimeType.split("/");
        String type;

        type = division[0];
        return type;
    }
 }
