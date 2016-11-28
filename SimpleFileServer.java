import java.io.*;
import java.util.concurrent.TimeUnit;
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
  private static ServerSocket servsock = null;
	private static boolean valpassword = true;
	private static boolean valusername = true;
	private static boolean login = false;
	private static boolean asciiArmor = false;
	private static ObjectOutputStream os;


  public static void main (String [] args ) throws IOException {
   FILE_TO_SEND = args[0];
   key = args[1];
   FileInputStream fis = null;
   BufferedInputStream bis = null;
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
	if(login){
       File myFile = new File (FILE_TO_SEND);                     //file to send to client
       String filePath =  myFile.getCanonicalPath().substring(0,  //get canonical path of client
        myFile.getCanonicalPath().lastIndexOf(File.separator));
       encryptor =  new Encryption(myFile, key);                  //init encryption
      int chunkCounter =  0;                                      //chunk counter
       int totalBytes = 0;                                        //counter for total bytes sent
	long start = System.currentTimeMillis();
       while(chunkCounter < encryptor.getChunkCounter()){         //makes sure all chunks are read
        //encrypts chunk using canonical path of original and stores it into a .temp (hidden folder). Format of chunk name: original.xxx
         byte[] chunkByteArray = encryptor.encryptChunk(encryptor.change(filePath +         
          "/.temp/" +FILE_TO_SEND + "." + String.format("%03d", chunkCounter)));            
        //prints out size of chunk and name of chunk
         System.out.println("Sending chunk: " + filePath + 
           "/.temp/" + FILE_TO_SEND + "." + String.format("%03d", chunkCounter) + "(" + chunkByteArray.length + " bytes)");  
         
         chunkCounter++;                                          //increment chunk counter
         if(asciiArmor)
		encryptor.asciiEncode(chunkByteArray);
         os.writeObject(chunkByteArray);      //writes chunk byte array to output stream
         os.flush();                                              //empties out outputstream
         totalBytes += chunkByteArray.length;                     //counts total bytes
       }
	long end = System.currentTimeMillis();
	long totalTime = TimeUnit.MILLISECONDS.toSeconds(end - start);
       System.out.println("\n***************************************************\n***************************************************");
       System.out.println("Sent: " + FILE_TO_SEND + " - Ascii Armored: " + asciiArmor +"\nTotal Bytes(Without headers: " + myFile.length() + "bytes // With headers: " + totalBytes + " bytes)"); 
       System.out.println("Completed in " + totalTime + " seconds.");
       System.out.println("***************************************************\n***************************************************\n");
	}
	else{
		if(!valusername){		
		String invU = "Invalid User.";
		os.writeObject(invU.getBytes());
		os.flush();
		}
		if(!valpassword){
		String invP = "Invalid Password.";
		os.writeObject(invP.getBytes());
		os.flush();
		}
	}
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
  if(username.equals(validUser) && password.equals(validPassword)){
   System.out.println("Welcome " + validUser + "!");
	
	login = true;
	valusername = true;
	valpassword = true;
	}
 else if(!username.equals(validUser)){
	login=false;         
	valusername = false;	
	} 
	else if(!password.equals(validPassword)){
	login=false;
	valpassword = false;
	}
	if(login){
			
		String val = "Welcome " + validUser + "!\nWould you like your data ascii armored? (y/n)";
		os.writeObject(val.getBytes());
		os.flush();

		output = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
		
		
  		String ascii = input.readLine();
if(ascii.equals("y"))
			asciiArmor = true;
		else if(ascii.equals("n"))
			asciiArmor = false;
	}
}
}
