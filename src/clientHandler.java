/**
 * Created by Yx on 10/10/2014.
 */
// Source : http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;


public class clientHandler {

    protected final String serverDir =  "server_data";
    protected SSLSocket clientSocket = null;
    protected String requestHeader = null;
    protected String getObject = null;
    protected String responseHeader = null;

    private InputStream inputStream = null;
    private File fileToSend = null;
    private FileInputStream fileInputStream = null;
    public BufferedInputStream bufferedInputStream = null;
    public BufferedOutputStream outputStream = null;
    private String responseType ;
    private String relativePathOfGET;


    public clientHandler(String request,SSLSocket socket) {
        System.out.println("Server Received: "+ request);
        this.requestHeader = request;
        this.getObject = "";
        this.responseType = "";
        this.relativePathOfGET = "";
        this.clientSocket = socket;

        parseHeader();
        composeResponse();
        sendResponse();
    }


    //http://stackoverflow.com/questions/3154488/best-way-to-iterate-through-a-directory-in-java

    private void sendResponse() {
        try {
            //this.fileToSend = new File("people.ucalgary.ca\\~mghaderi\\cpsc441\\test\\a.html");
            if (this.fileToSend != null && this.responseType == "file") {
                fileInputStream = new FileInputStream(this.fileToSend);
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                this.outputStream = new BufferedOutputStream(this.clientSocket.getOutputStream());

                System.out.println("Sending: "+fileToSend.getName() +" to client");
                // Writer:
                byte[] writerBuffer = new byte[(int) this.fileToSend.length()];
                bufferedInputStream.read(writerBuffer, 0, writerBuffer.length);
                //System.out.println("Sending " + "(" + writerBuffer.length + " bytes)");
                outputStream.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                //System.out.println("Writing: "+new String(writerBuffer));
                outputStream.write(writerBuffer, 0, writerBuffer.length);
                //System.out.println("length:" + writerBuffer.length + " --- "+new String(writerBuffer));
                outputStream.flush();

                System.out.println("Sent.");

                //close streams right after sent response
                fileInputStream.close();
                bufferedInputStream.close();
                //inputStream.close();
                //outputStream.close();
            } else if(this.responseType=="404"){
                outputStream = new BufferedOutputStream(this.clientSocket.getOutputStream());
                System.out.println("Sending " + "(" + this.requestHeader.getBytes().length + " bytes)");
                outputStream.write(this.responseHeader.getBytes());
                //System.out.println("Writing: "+new String(writerBuffer));
                String NotFound = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head>\n" +
                        "<title>My Bad! </title>\n" +
                        "</head><body>\n" +
                        "<h1>My Bad!!(404 Not Found)</h1>\n" +
                        "<p>The requested URL "+this.getObject+" was not found on this server. Life is hard yeah?</p>\n" +
                        "<hr>\n" +
                        "</body></html>\n";
                outputStream.write(NotFound.getBytes());
                outputStream.flush();
                inputStream.close();
                outputStream.close();
                //System.out.println("404 handler");
            }else if(this.responseType == "dir"){
                String[] inDir = new String[this.fileToSend.listFiles().length];
                String dirList = "";
                for(int i=0;i<this.fileToSend.listFiles().length;i++){
                    if(this.relativePathOfGET.equalsIgnoreCase("/")){
                        inDir[i] = "<a style=\"color: #00F\"" +
                                "\" onMouseOver=\"this.style.color='#0F0'\""+" onMouseOut=\"this.style.color='#00F'\" href=\""+"/"+this.fileToSend.listFiles()[i].getName()+"\">"+this.fileToSend.listFiles()[i].getName()+"</a><br/>";
                    }else{
                        inDir[i] = "<a style=\"{font-weight:bold;color: #00F}\n\""+"onMouseOver=\"this.style.color='#0F0'\"\n" +
                                "   onMouseOut=\"this.style.color='#00F'\" href=\"" +this.relativePathOfGET+"/"+this.fileToSend.listFiles()[i].getName()+"\">"+this.fileToSend.listFiles()[i].getName()+"</a><br/>";
                    }
                    dirList+=inDir[i];
                    System.out.println("-> " + this.fileToSend.listFiles()[i].getName());
                }

                String responseWithDirList = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\"><html><head>\n" +
                        "<title>In Directory List:</title>\n" +
                        "</head><body>\n" +
                        "<h1>Found Directory: </h1>\n<div style=\"font-weight:bold;\">" + dirList +
                        "<hr>\n" +
                        "</div></body></html>\n";
                System.out.println(responseWithDirList);
                outputStream = new BufferedOutputStream(this.clientSocket.getOutputStream());
                outputStream.write(this.responseHeader.getBytes());
                outputStream.write(responseWithDirList.getBytes());
                outputStream.flush();
                inputStream.close();
                outputStream.close();
            }else if(this.responseType == "upload"){
                //System.out.println("Handles Upload");
                this.outputStream = new BufferedOutputStream(this.clientSocket.getOutputStream());
                this.outputStream.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                this.outputStream.flush();
                //this.outputStream.close();
            }

        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

    private void composeResponse(){
        this.responseHeader = "";
        if(this.getObject.equalsIgnoreCase("")){
            System.out.println("Get object is empty...");
        }else {
            if(this.requestHeader.contains("GET")){
                File getFile = new File(getObject);
                if(getFile.exists()){
                    // if file or dir exists
                    compose200ResponseHeader();
                    this.fileToSend = getFile;
                    //System.out.println(getObject+" exits.");
                    if (getFile.isDirectory()) {
                        this.responseType = "dir";
                    } else if (getFile.isFile()){
                        this.responseType = "file";
                        System.out.println("File: " + getFile.getName() + " located.");
                    }
                }else{ // 404 hanlder
                    this.responseType = "404";
                    compose404ResponseHeader();
                    //System.out.println(getObject+" doesn't exits");
                }

            }else if(this.requestHeader.contains("PUT")){
                this.responseType = "upload";
                compose200ResponseHeader();
            }
        }
    }



    // ______________________________________________
    // helpers functions ::
    private void parseHeader(){
        //System.out.println(this.requestHeader);
        if(this.requestHeader.contains("GET")) {
            String lines[] = this.requestHeader.split("\\r?\\n");
            //this.headerArray = new String[lines.length];
            for(int i = 0; i < lines.length; i++) {
                if(lines[i].contains("GET")){
                    String currentLine = lines[i];
                    String[] headerContent = currentLine.split(" ");
                    this.getObject = this.serverDir+headerContent[1].trim();
                    this.relativePathOfGET = headerContent[1].trim();
                }
            }
        }else if(this.requestHeader.contains("PUT")){
            String lines[] = this.requestHeader.split("\\r?\\n");
            for(int i = 0; i < lines.length; i++) {
                if(lines[i].contains("PUT")){
                    String currentLine = lines[i];
                    String[] headerContent = currentLine.split(" ");
                    this.getObject = this.serverDir+headerContent[1].trim();
                    this.relativePathOfGET = headerContent[1].trim();
                }
            }
        }
    }
    private void compose200ResponseHeader(){
        this.responseHeader = "HTTP/1.1 200 OK\r\n\r\n";
    }
    private void compose404ResponseHeader(){
        this.responseHeader = "HTTP/1.1 404 Not Found\r\n\r\n";
    }
}