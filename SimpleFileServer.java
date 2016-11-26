import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleFileServer {

  public final static int SOCKET_PORT = 13267;  // you may change this
  public static String FILE_TO_SEND;  // you may change this

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
    OutputStream os = null;
    ServerSocket servsock = null;
    try {
      servsock = new ServerSocket(SOCKET_PORT);
      while (true) {
        System.out.println("Waiting...");
        int chunkCounter =  0;
        try {
          sock = servsock.accept();
          System.out.println("Accepted connection : " + sock);
          // send file
			try{
				logInfo();
			} catch(Exception e) {
			e.printStackTrace();
			}
			
          File myFile = new File (FILE_TO_SEND);
  	  String filePath =  myFile.getCanonicalPath().substring(0, 
		myFile.getCanonicalPath().lastIndexOf(File.separator));
          encryptor =  new Encryption(myFile, key);
          int totalBytes = 0;
          while(chunkCounter < encryptor.getChunkCounter()){
        	byte[] chunkByteArray = encryptor.encryptChunk(encryptor.change(filePath + 
			"/.temp/" +FILE_TO_SEND + "." + String.format("%03d", chunkCounter)));
        	System.out.println("Sending " + filePath + 
			"/.temp/" + FILE_TO_SEND + "." + String.format("%03d", chunkCounter) + "(" + chunkByteArray.length + " bytes)");  
           	chunkCounter++;
           	os = sock.getOutputStream();
          	os.write(chunkByteArray, 0, chunkByteArray.length);
          	os.flush();
		totalBytes += chunkByteArray.length;
          }
	System.out.println("\n***************************************************\n***************************************************");
          System.out.println("Sent: " + FILE_TO_SEND + "\n(" + totalBytes + " bytes)");  
          System.out.println("Done!");
	System.out.println("***************************************************\n***************************************************\n");
        }
        finally {
          if (bis != null) bis.close();
          if (os != null) os.close();
          if (sock!=null) sock.close();
        }
      }
    }
    finally {
      if (servsock != null) servsock.close();
    }
  }

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
