import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleFileServer {

  public final static int SOCKET_PORT = 13267; 
  public static String FILE_TO_SEND; 

  public static String key;
  public  static Encryption encryptor;
  public final static String validUser = "mark";
  public final static String validPassword = "ilog";
  public static BufferedReader input = null;
  public static PrintWriter output = null;
  public static Socket sock = null;

  public static void main (String [] args ) throws IOException {
   FILE_TO_SEND = args[0];
   key = args[1];
   FileInputStream fis = null;
   BufferedInputStream bis = null;
   ObjectOutputStream os = null;
   ServerSocket servsock = null;
   try {
    servsock = new ServerSocket(SOCKET_PORT);                     //creates server socket
    while (true) {
      System.out.println("Waiting...");
      try {
        sock = servsock.accept();                                 //waits for a client, creates a socket once one is found
os = new ObjectOutputStream(sock.getOutputStream());                             //init socket output stream
        System.out.println("Accepted connection : " + sock);      //prints out client info
        try{
          logInfo();                                              //verifies client as a valid user
        } catch(Exception e) {
         e.printStackTrace();
       }

       File myFile = new File (FILE_TO_SEND);                     //file to send to client
       String filePath =  myFile.getCanonicalPath().substring(0,  //get canonical path of client
        myFile.getCanonicalPath().lastIndexOf(File.separator));
       encryptor =  new Encryption(myFile, key);                  //init encryption
      int chunkCounter =  0;                                      //chunk counter
       int totalBytes = 0;                                        //counter for total bytes sent
       while(chunkCounter < encryptor.getChunkCounter()){         //makes sure all chunks are read
        //encrypts chunk using canonical path of original and stores it into a .temp (hidden folder). Format of chunk name: original.xxx
         byte[] chunkByteArray = encryptor.encryptChunk(encryptor.change(filePath +         
          "/.temp/" +FILE_TO_SEND + "." + String.format("%03d", chunkCounter)));            
        //prints out size of chunk and name of chunk
         System.out.println("Sending " + filePath + 
           "/.temp/" + FILE_TO_SEND + "." + String.format("%03d", chunkCounter) + "(" + chunkByteArray.length + " bytes)");  
         
         chunkCounter++;                                          //increment chunk counter
         
         os.writeObject(chunkByteArray);      //writes chunk byte array to output stream
         os.flush();                                              //empties out outputstream
         totalBytes += chunkByteArray.length;                     //counts total bytes
       }
       System.out.println("\n***************************************************\n***************************************************");
       System.out.println("Sent: " + FILE_TO_SEND + "\nTotal Bytes(Without headers: " + myFile.length() + "bytes // With headers: " + totalBytes + " bytes)");  
       System.out.println("Done!");
       System.out.println("***************************************************\n***************************************************\n");
     }
     finally {
      //closes everything
      if (bis != null) bis.close();
      if (os != null) os.close();
      if (sock!=null) sock.close();
    }
  }
}
finally {
  //closes server socket
  if (servsock != null) servsock.close();
}
}
      //verifies client as a valid user//
      ///////////////////////////////////
public static void logInfo() throws Exception{            
  input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
  String username = input.readLine(); 
  System.out.println("SERVER USER LOGIN: " + username );
  String password = input.readLine();
  if(username.equals(validUser) && password.equals(validPassword))
   System.out.println("Welcome " + validUser + "!");
 else
   System.out.println("Invalid User");
 output = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
}
}
