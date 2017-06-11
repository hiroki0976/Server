import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class ServerThread implements Runnable{
    private static final String DOCUMENT_ROOT = "/Users/fujioka/HTML/";
    private Socket socket;

    private static String readLine(InputStream input) throws Exception {
        int ch;
        String ret = "";
        while ((ch = input.read()) != -1) {
            if (ch == '\r') {

            } else if (ch == '\n') {
                break;
            } else {
                ret += (char)ch;
            }
        }

        if (ch == -1) {
            return null;
        } else {
            return ret;
        }
    }

    public static void writeLine(OutputStream output, String str) throws Exception {
        for (char ch : str.toCharArray()) {
            output.write((int)ch);
        }
        output.write((int)'\r');
        output.write((int)'\n');
    }

    public static String getDate() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyy HH:mm:ss", Locale.US);
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime()) + " GMT";
    }

    private static final HashMap<String, String> contentTypeMap = new HashMap<String, String>() {
        {
            put("html", "text/html");
            put("css", "text/css");
            put("javascript", "text/javascript");
            put("jpeg", "image/jpeg");
        }
    };

    public static String getContentType(String ext) {
        String ret = contentTypeMap.get(ext.toLowerCase());
        if (ret == null) {
            return "application/octet-stream";
        } else {
            return ret;
        }
    }


    @Override
    public void run() {
        OutputStream output;
        try {
            InputStream input = socket.getInputStream();

            String line;
            String path = null;
            String ext = null;
            while ((line = readLine(input)) != null) {
                if(line == "") break;
                if (line.startsWith("GET")) {
                    path = line.split(" ")[1];
                    String[] tmp = path.split("\\.");
                    ext = tmp[tmp.length - 1];
                }
            }

            output = socket.getOutputStream();

            try (FileInputStream fis = new FileInputStream(DOCUMENT_ROOT + path)) {
                SendResponse.SendOkResponse(output, fis, ext);
            } catch (FileNotFoundException ex) {
                SendResponse.SendNotFoundResponse(output, DOCUMENT_ROOT);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    ServerThread(Socket socket) {
        this.socket = socket;
    }
}
