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
		public static int totalReceivedSize = 0;

	  public static void main (String [] args ) throws Exception {
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
	  		ObjectInputStream is = new ObjectInputStream(sock.getInputStream()); 			//receives any input from predetermined socket
	  		fos = new FileOutputStream(FILE_TO_RECEIVED);  		//will output the chunks to a file
	  		bos = new BufferedOutputStream(fos); 				//will output bytes to file output
	  			decryptor = new Decryption(key); 				//init decryption
	  		int totalBytes = 0; 								//used to count total bytes of file
	  		int byteRead = -1;									//used to store next byte in inputstream
			int receivedBytes = 0;
	  		boolean chunkSizeFail = false;
			boolean hashFail = false;
			boolean fail = false;
			boolean exit = false;
			Object temp;
	  			do{					//conditional value is predetermined chunk size!!!
					try{
						temp = is.readObject();	  				
						byte[] receivedChunk  = (byte[])temp;						//reads next byte, .read() returns -1 if not byte is found
						byte[] decrypted = decryptor.decrypt(receivedChunk);
						String s = new String(decrypted);
						//System.out.println(s);
						String[] result = s.split("%%");
						if(Integer.parseInt(result[0]) != result[3].length()){
							chunkSizeFail = true;
							fail = true;
						}
						receivedBytes = Integer.parseInt(result[1]);
						byte[] hashable = result[3].getBytes();	
						hashable = decryptor.hash(hashable);
						String hashed = new String(hashable);
						//System.out.println(result[2]+"/////"+hashed);
						if(!result[2].equals(hashed)){
							hashFail = true;
							fail = true;
						}			
						if(!fail){
							byte[] output = result[3].getBytes();
							totalBytes += output.length;
							bos.write(output, 0, output.length);
							bos.flush();
						}
						else{
							if(chunkSizeFail)
								System.out.println("Chunk size incorrect!");
							if(hashFail)
								System.out.println("Hash incorrect!");
						}
								
					}catch (Exception e){
						exit = true;					
					}	 
	  			}while(!exit);
	  			if(totalBytes == receivedBytes)
					System.out.println("Successfully downloaded file " + FILE_TO_RECEIVED  + " downloaded (" + totalBytes + " bytes  / " + receivedBytes+ " bytes )");
				else
					System.out.println("Failed to received full file! ( " + totalBytes + " bytes downloaded / " + receivedBytes + " bytes expected)!");
	
					
	  			
	  	}
	  	finally {
	  		//close everything used
	  		if (fos != null) fos.close();
	  		if (bos != null) bos.close();
	  		if (sock != null) sock.close();
	  	}
	  }

	}
