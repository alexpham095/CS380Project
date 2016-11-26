package encryption;
import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.net.Socket;
import javax.swing.JOptionPane;
import java.util.ArrayList;

public class SimpleFileClient {

  public final static int SOCKET_PORT = 13267;      
  public static String SERVER; 
  public static String FILE_TO_RECEIVED;
  private static Scanner reader = new Scanner(System.in);
  private static  PrintWriter output;

  private static Console console = System.console();
  public static String key;
  public static Decryption decryptor;
  public static ArrayList<Byte> receivedChunks =  new ArrayList<Byte>(); //needs to be ArrayList since the size will be dynamic

  public static void main (String [] args ) throws IOException {
  	SERVER = args[0]; 					//ip address of server
  	FILE_TO_RECEIVED = args[1];			//file to write received data to
  	key = args[2];						//key
  	int bytesRead;
  	int current = 0;
  	FileOutputStream fos = null;
  	BufferedOutputStream bos = null;
  	Socket sock = null;
  	try {
  		sock = new Socket(SERVER, SOCKET_PORT);				//attempts to connect to predetermined server with predetermined port

  		//Attempts to login//
  		System.out.println("Connecting...");
  		output = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
  		System.out.println("Enter Username: ");
  		String username = reader.nextLine();
  		output.println(username);
  		String password = new String(console.readPassword("Enter Password: "));
  		output.println(password);
  		output.flush();
  		/////////

  		//Receiving chunks//
  		InputStream is = sock.getInputStream(); 			//receives any input from predetermined socket
  		fos = new FileOutputStream(FILE_TO_RECEIVED);  		//will output the chunks to a file
  		bos = new BufferedOutputStream(fos); 				//will output bytes to file output
  			decryptor = new Decryption(key); 				//init decryption
  		int totalBytes = 0; 								//used to count total bytes of file
  		int byteRead = -1;									//used to store next byte in inputstream
  		boolean fail = false;
  		do{
  			for(int i = 0; i < 256; i++){					//conditional value is predetermined chunk size!!!
  				byteRead = is.read();						//reads next byte, .read() returns -1 if not byte is found
  				if(byteRead != -1)
  					receivedChunks.add((byte)byteRead); 	//adds read byte to chunk array list
  			}
  			byte[] decrypted = new byte[receivedChunks.size()]; //byte array to store decrypted version
  			for(int j = 0; j<receivedChunks.size(); j++){   
  				decrypted[j] = receivedChunks.get(j);		//stores undecrypted version to decrypted
  			}
  			decrypted = decryptor.decrypt(decrypted); 		//decrypts decrypted
  			String s = new String(decrypted);
  			String[] result = s.split("%%");				//split string
  			byte[] hashable = result[3].getBytes();			//get data to hash
  			hashable = decryptor.hash(hashable);			//Hash
  			String hashed = new String(hashable);
  			if(Integer.parseInt(result[0]) != 256){			//compare size of chunk
  				fail = true;
  			}
  			else if(Integer.parseInt(result[1]) != decrypted.length){ //compare full size of file
  				fail = true;
  			}
  			else if( hashed != result[2] ){					//compare Hash
  				fail = true;
  			}
  			bos.write(decrypted,0,decrypted.length); 		//writes decrypted chunk to buffered output stream
  			bos.flush();									//empties out buffered output stream
  			totalBytes += receivedChunks.size();			//counter for total bytes
  			receivedChunks = new ArrayList<Byte>(); 		//resets array list for next chunk
  		}while(byteRead != -1);								//stops if there is nothing left to read
  		System.out.println("File " + FILE_TO_RECEIVED  + " downloaded (" + totalBytes + " bytes read)");
  	}
  	finally {
  		//close everything used
  		if (fos != null) fos.close();
  		if (bos != null) bos.close();
  		if (sock != null) sock.close();
  	}
  }

}