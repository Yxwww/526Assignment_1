/**
 * Created by Yuxibro on 15-02-20.
 */
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.*;

public class C_2_Server {
    // Key store infomation
    final static String keyStorePath = "key/test1/server.keystore";
    final static String password = "yuxibruh"; // The Password
    final static int serverPort = 3456;
    final static boolean debugEnabled = false;   // enable debug mode

    protected static final String serverDir =  "server_data";
    protected static String serverText = "";
    protected static String requestHeader = "";
    protected static String getObject = "";
    protected static String responseHeader = "";

    private static InputStream inputStream = null;
    private static File fileToSend = null;
    private static FileInputStream sslIS = null;
    //BufferedInputStream bufferedInputStream = null;
    //OutputStream sslOS = null;
    private static OutputStream sslOS = null;
    private static String responseType = "" ;
    private static String relativePathOfGET = "";


    public static void main(String[] arstring) {
        try {
            System.setProperty("javax.net.ssl.keyStore", keyStorePath);     // trust keystore
            System.setProperty("javax.net.ssl.keyStorePassword", password); // keystore password
            if (debugEnabled){
                System.setProperty("javax.net.debug", "all");
            }

            SSLServerSocketFactory server = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) server.createServerSocket(serverPort);

            SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
            InputStream sslIS = sslSocket.getInputStream();

            //For Writing Back to the Client
            sslOS = sslSocket.getOutputStream();

            //Read from the Client
            BufferedReader bufferedreader = new BufferedReader( new InputStreamReader(sslIS));
            BufferedInputStream inputStream = new BufferedInputStream(sslIS);
            //System.out.println("Server listening on port" + serverPort);
            byte[] readerBuffer = new byte[1024];
            /*while ((string = bufferedreader.readLine()) != null) {
                if(string!="" && string != "SHUTDOWN"){
                    clientHandler client = new clientHandler(string);
                }else{
                    break;
                }
            }*/
            System.out.println("Server started on localhost:"+ serverPort);
            String readString = null;
            clientHandler client = null;
            while(inputStream.read(readerBuffer)!=-1) {
                readString = new String(readerBuffer);
                if (readString.trim().equals("SHUTDOWN")){
                    System.out.println("SHUTDOWN message received... ");
                    break;
                }
                if(!readString.equals("")){
                    client = new clientHandler(readString,sslSocket);
                }
                System.out.println("");
                readerBuffer = new byte[1024];
            }

            inputStream.close();
            //client.outputStream.close();
            System.out.flush();
            sslSocket.close();


        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
