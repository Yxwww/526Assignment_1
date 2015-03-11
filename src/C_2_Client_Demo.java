import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.*;

public class C_2_Client_Demo {

    final static String trustStorePath = "key/test1/clientcert";//ia.jks";
    final static String password = "yuxibruh"; // The Password
    final static String server = "localhost";
    final static int serverPort = 3456;
    final static boolean debugEnabled = false;   // enable debug mode

    final static String fileToSend = "ClientToSend/lifeishard.txt";

    static SSLSocket sslSocket = null;
    static OutputStream sslOS;
    static InputStream sslIS;
    static BufferedInputStream inputStream;
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
            sslSocket = (SSLSocket) sslsf.createSocket(server,serverPort);
            sslOS = sslSocket.getOutputStream();
            sslIS = sslSocket.getInputStream();

            //Read from the Client
            inputStream  = new BufferedInputStream(sslIS);


            //First get msg
            System.out.println("# Send msg: GET /hello.txt HTTP/1.0 to server ");
            sslOS.write("GET /hello.txt HTTP/1.0\r\n\r\n".getBytes());
            sslOS.flush();
            //Read from response
            System.out.println("Getting response from server");
            readFromServer();
            // Send put message.
            putCommandHandler();
            readFromServer();
            sslOS.flush();

            // Send shutdown msg.
            System.out.println("# Send shutdown msg..");
            sslOS.write("SHUTDOWN".getBytes());
            sslOS.flush();
            sslSocket.close();
            sslOS.close();
            sslIS.close();
            System.out.println("Done.");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    // Handles put command
    public static void putCommandHandler(){
        try {
            // locate the file that is trying to send
            File FileToPut = new File(fileToSend);
            // if file exits
            if (FileToPut.exists()) {
                // if file exists we put it in header command and send to server.
                FileInputStream fileInputStream = new FileInputStream(FileToPut);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                BufferedOutputStream outputStream = new BufferedOutputStream(sslOS);
                System.out.println("# send msg: PUT ClientToSend/lifeishard.txt  to server" );
                byte[] writerBuffer = new byte[(int) FileToPut.length()];
                bufferedInputStream.read(writerBuffer, 0, writerBuffer.length);
                outputStream.write(("PUT " + fileToSend + "\r\n\r\n").getBytes());
                outputStream.write(writerBuffer, 0, writerBuffer.length);
                outputStream.flush();
                System.out.println("Sent.");

                //close streams right after sent response
                fileInputStream.close();
                bufferedInputStream.close();

            } else { // 404 hanlder
                System.out.println("the file you are trying to put doesn't exit on your machine. " + fileToSend);
                //System.out.println(getObject+" doesn't exits");
            }
        }catch(Exception e ){
            System.out.println("Cannot send file out due to: "+ e);
        }
    }

    public static void readFromServer(){
        try {
            System.out.println();
            byte[] buffer = new byte[1024];
            int readBytes = -1;
            readBytes = inputStream.read(buffer);
            System.out.println("bytes read:" + readBytes);
            if (readBytes < buffer.length) {    // handles small files
                if (new String(buffer) != "") {
                    System.out.println(new String(buffer));
                }
                System.out.println("bytes read:" + readBytes);
            } else {
                while (readBytes == buffer.length) {
                    if (new String(buffer) != "") {
                        System.out.println(new String(buffer));
                    }
                    buffer = new byte[1024];
                    readBytes = inputStream.read(buffer);
                    //System.out.println("bytes read:" + readBytes);
                }
            }
        }catch( Exception e ){
            System.out.println("Unable to read from server due to:"+e);
        }
    }
}