import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    /** Print on screen the type of error.
    * HTTP/1.1 404 Not Found
    Content-Length: 1635
    Content-Type: text/html
    Server: Microsoft-IIS/6.0
    X-Powered-By: ASP.NET
    Date: Tue, 04 May 2010 22:30:36 GMT
    Connection: close*/
    public void printNotFound(PrintWriter printWriter){
        String stringFile = getStringFile("NotFound.html"); //Ask for the file and converts into string

        printWriter.println("HTTP/1.1 404 Not Found");
        printWriter.println("Content-Length: " + stringFile.length());
        printWriter.println("Content-Type: text/html");
        printWriter.println("Server: JavaServer");
        printWriter.println("Date: " + new Date());
        printWriter.println("Connection: close");
        printWriter.println();
        printWriter.println(stringFile); //Content
    }

    //Writes file into socket
    private String getStringFile(String file) {
        String stringFile = "";

        try {
            InputStream inFile = this.getClass().getClassLoader()
                    .getResourceAsStream(file);
            stringFile = new BufferedReader(new InputStreamReader(inFile))
                    .lines().collect(Collectors.joining("\n"));

        } catch (Exception e) {
            System.out.println(e);
        }

        return stringFile;
    }
 }
