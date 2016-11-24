package src;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleFileServer {

  public final static int SOCKET_PORT = 13267;  // you may change this
  public final static String FILE_TO_SEND = "/home/client/Documents/send.txt";  // you may change this

  public static String key = "key.txt";
  public  static Encryption encryptor;
	public final static String validUser = "danielchow";
	public final static String validPassword = "fourfourfourfour";
	public static BufferedReader input = null;
	public static PrintWriter output = null;
    public static Socket sock = null;

  public static void main (String [] args ) throws IOException {
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
    ServerSocket servsock = null;
    try {
      servsock = new ServerSocket(SOCKET_PORT);
      while (true) {
        System.out.println("Waiting...");
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
          encryptor =  new Encryption(myFile, key);
          
          int chunkCounter =  0;
          while(chunkCounter < encryptor.getChunkCounter()){							//chunk be pushed one at a time
        	byte[] chunkByteArray = encryptor.encryptChunk(encryptor.change(FILE_TO_SEND + "." + String.format("%03d", chunkCounter)));
        	System.out.println("Sending " + FILE_TO_SEND + "." + String.format("%03d", chunkCounter) + "\n(" + chunkByteArray.length + " bytes)");  
            chunkCounter++;
            os = sock.getOutputStream();
          	os.write(chunkByteArray, 0, chunkByteArray.length);
          	os.flush();
          }
          //fis = new FileInputStream(myFile);
          //bis = new BufferedInputStream(fis);
         // bis.read(mybytearray,0,mybytearray.length);
         // os = sock.getOutputStream();
         // System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
          //os.write(mybytearray,0,mybytearray.length);
          //os.flush();
          System.out.println("Done.");
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
		System.out.println("SERVER USER PASSWORD: " + password);
		if(username.equals(validUser) && password.equals(validPassword))
			System.out.println("Welcome " + validUser + "!");
		else
			System.out.println("Invalid User");
		output = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
}
}
