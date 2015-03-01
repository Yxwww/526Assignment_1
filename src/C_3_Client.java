import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class C_3_Client {

    final static String trustStorePath = "key/test1/clientcert";//ia.jks";
    final static String password = "yuxibruh"; // The Password
    final static String server = "localhost";
    final static int serverPort = 3456;
    final static boolean debugEnabled = true;   // enable debug mode


    public static void main(String[] arstring) {
        try {
            byte[] sendBytes, receiveBytes = new byte[1000];
            // set trust property and
            System.setProperty("javax.net.ssl.trustStore", trustStorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", password);
            if (debugEnabled){
                System.setProperty("javax.net.debug", "all");
            }
            SSLSocketFactory sslsf = (SSLSocketFactory) SSLSocketFactory
                    .getDefault();
            SSLSocket sslSocket = (SSLSocket) sslsf.createSocket(server,serverPort);
            OutputStream sslOS = sslSocket.getOutputStream();

            InputStream sslIS = sslSocket.getInputStream();

            //Read from the Client
            BufferedReader bufferedreader = new BufferedReader( new InputStreamReader(sslIS));
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));   // read user input
            sslOS.write("Hello SSL Server".getBytes()); // Write to the Server
            //http://www.rgagnon.com/javadetails/java-0542.html
            // TODO: File transfer
            sslOS.flush();
            sslSocket.close();


        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}