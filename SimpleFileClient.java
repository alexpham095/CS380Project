import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.net.Socket;
import javax.swing.JOptionPane;
import java.util.ArrayList;

public class SimpleFileClient {

  public final static int SOCKET_PORT = 13267;      // you may change this
  public static String SERVER;  // localhost
  public static String
       FILE_TO_RECEIVED;  // you may change this, I give a
                                                            // different name because i don't want to
                                                            // overwrite the one used by server...
	private static Scanner reader = new Scanner(System.in);
	private static  PrintWriter output;

	private static Console console = System.console();
	public static String key;
	public static Decryption decryptor;
	public static ArrayList<Byte> receivedChunks =  new ArrayList<Byte>();
  public static void main (String [] args ) throws IOException {
    SERVER = args[0];
	FILE_TO_RECEIVED = args[1];
	key = args[2];
    int bytesRead;
    int current = 0;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    Socket sock = null;
    try {
      sock = new Socket(SERVER, SOCKET_PORT);
      System.out.println("Connecting...");
		output = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
	System.out.println("Enter Username: ");
	String username = reader.nextLine();
	output.println(username);
	String password = new String(console.readPassword("Enter Password: "));
	output.println(password);
	output.flush();
      // receive file
      InputStream is = sock.getInputStream();
      fos = new FileOutputStream(FILE_TO_RECEIVED);
      bos = new BufferedOutputStream(fos);
      byte [] mybytearray;
      decryptor = new Decryption(key);
	int totalBytes = 0;
	int byteRead = -1;
    	do{
		for(int i = 0; i < 256; i++){			
			byteRead = is.read();
			if(byteRead != -1)
			    receivedChunks.add((byte)byteRead);
		}
		byte[] decrypted = new byte[receivedChunks.size()];
		for(int j = 0; j<receivedChunks.size(); j++){
			decrypted[j] = receivedChunks.get(j);
		}
		decrypted = decryptor.decrypt(decrypted);
		bos.write(decrypted,0,decrypted.length);
		bos.flush();
		totalBytes += receivedChunks.size();
		receivedChunks = new ArrayList<Byte>();
	}while(byteRead != -1);
      		System.out.println("File " + FILE_TO_RECEIVED  + " downloaded (" + totalBytes + " bytes read)");
    }
    finally {
      if (fos != null) fos.close();
      if (bos != null) bos.close();
      if (sock != null) sock.close();
    }
  }

}
