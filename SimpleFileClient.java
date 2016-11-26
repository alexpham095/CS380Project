import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.net.Socket;
import javax.swing.JOptionPane;

public class SimpleFileClient {

  public final static int SOCKET_PORT = 13267;      // you may change this
  public final static String SERVER = "192.168.56.104";  // localhost
  public final static String
       FILE_TO_RECEIVED = "/home/client/Desktop/cs380/received.txt";  // you may change this, I give a
                                                            // different name because i don't want to
                                                            // overwrite the one used by server...
	private static Scanner reader = new Scanner(System.in);
	private static  PrintWriter output;
  public final static int FILE_SIZE = 10000000; // file size temporary hard coded
                                               // should bigger than the file to be downloaded
	private static Console console = System.console();
	public final static String key = "key.txt";
	public static Decryption decryptor;
  public static void main (String [] args ) throws IOException {
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
      //bytesRead = is.read(mybytearray,0,mybytearray.length);
      //current = bytesRead;
      decryptor = new Decryption(key);
	//do {
      	//bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
      	//if(bytesRead >= 0) 
	//	current += bytesRead;
	//byte[] decrypted = decryptor.decrypt(mybytearray);
      	//bos.write(decrypted, 0 , current);
	//System.out.println(bytesRead);
      	//bos.flush();
      	//System.out.println("File " + FILE_TO_RECEIVED
        //  + " downloaded (" + current + " bytes read)");
      	//} while(bytesRead > -1);
    	do{
		mybytearray = new byte[4];
		for(int i = 0; i < mybytearray.length; i++){
			mybytearray[i] = (byte)is.read();
		}
		byte[] decrypted = decryptor.decrypt(mybytearray);
		bos.write(decrypted,0,decrypted.length);
      		System.out.println("File " + FILE_TO_RECEIVED  + " downloaded (" + decrypted.length + " bytes read)");
		bos.flush();
	}while(is.read() != -1);
    }
    finally {
      if (fos != null) fos.close();
      if (bos != null) bos.close();
      if (sock != null) sock.close();
    }
  }

}
