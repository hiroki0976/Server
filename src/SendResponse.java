import java.io.FileInputStream;
import java.io.OutputStream;

public class SendResponse{
    static void SendOkResponse(OutputStream output, FileInputStream fis, String ext) throws Exception {
        ServerThread.writeLine(output, "HTTP/1.1 200 OK");
        ServerThread.writeLine(output, "Date: " + ServerThread.getDate());
        ServerThread.writeLine(output, "Server: Server.java");
        ServerThread.writeLine(output, "Connection: close");
        ServerThread.writeLine(output, "Content-type: " + ServerThread.getContentType(ext));
        ServerThread.writeLine(output, "");

        int ch;
        while ((ch = fis.read()) != -1) {
            output.write(ch);
        }
    }

    static void SendNotFoundResponse(OutputStream output, String errorDocumentRoot) throws Exception {
        ServerThread.writeLine(output, "HTTP/1.1 404 Not Found");
        ServerThread.writeLine(output, "Date: " + ServerThread.getDate());
        ServerThread.writeLine(output, "Server: Server.java");
        ServerThread.writeLine(output, "Connection: close");
        ServerThread.writeLine(output, "Content-type: text/html");
        ServerThread.writeLine(output, "");

        try (FileInputStream fis = new FileInputStream(errorDocumentRoot + "404.html")) {
            int ch;
            while ((ch = fis.read()) != -1) {
                output.write(ch);
            }
        }
    }
}
