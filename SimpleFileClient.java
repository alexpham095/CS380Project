	import java.util.Scanner;
	import java.io.*;
	import java.net.*;
	import java.net.Socket;
	import javax.swing.JOptionPane;
	import java.util.ArrayList;
	import java.util.concurrent.TimeUnit;
	import java.nio.charset.Charset;
	import java.lang.Thread;

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
		private static boolean asciiArmor = false;
		private static boolean succession = false;
		private static boolean decrypt = false;

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
			int p = 0;
			while(!succession && p<3){ 
	  		sock = new Socket(SERVER, SOCKET_PORT);				//attempts to connect to predetermined server with predetermined port
	ObjectInputStream is = new ObjectInputStream(sock.getInputStream()); 			
	  		//Attempts to login//
	  		System.out.println("Connecting...");
	  		output = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
		 		
			System.out.println("Enter Username: ");
	  		String username = reader.nextLine();
	  		output.println(username);
	  		String password = new String(console.readPassword("Enter Password: "));
	  		output.println(password);
			output.flush();

	  		//Receiving chunks//
	  	//receives any input from predetermined socket
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
			byte[] validation = (byte[])is.readObject();
			String x = new String(validation);			
			System.out.println(x);
			String ascii = reader.nextLine();
			if(ascii.equals("y"))
				asciiArmor = true;
			else
				asciiArmor = false;
			output.println(ascii);
			output.flush();
				long start = System.currentTimeMillis();
				int counter = 0;
	  			do{					//conditional value is predetermined chunk size!!!
					try{	
						temp = is.readObject();	  				
			 			byte[] receivedChunk  = (byte[])temp;						//reads next byte, .read() returns -1 if not byte is found
						if(asciiArmor)
							decryptor.asciiDecode(receivedChunk);
						byte[] decrypted = decryptor.decrypt(receivedChunk);

						String s = new String(decrypted, "UTF-8");
						//System.out.println(s);
						String[] result = s.split("%%");
						int headerLength = decrypted.length - Integer.parseInt(result[0]);
						byte[] woheader = new byte[decrypted.length-headerLength];
						//System.out.println(decrypted.length);
						//System.out.println(counter);
						counter++;				
						for(int k = 0; k<woheader.length; k++){
							woheader[k] = decrypted[k+headerLength];
						}
						//System.out.println(result[0] + "////" +woheader.length);
						if(Integer.parseInt(result[0]) != woheader.length){
							chunkSizeFail = true;
							fail = true;
						}
						receivedBytes = Integer.parseInt(result[1]);
						byte[] hashable = woheader;
						hashable = decryptor.hash(hashable);
						String hashed = new String(hashable);
						if(!result[2].equals(hashed)){
							hashFail = true;
							//fail = true;
						}			
						if(!fail){
							byte[] output = woheader;
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
				long end = System.currentTimeMillis();
				long totalTime = TimeUnit.MILLISECONDS.toSeconds(end-start);
	  			if(totalBytes == receivedBytes && (receivedBytes + totalBytes) != 0){
					System.out.println("Successfully downloaded file into " + FILE_TO_RECEIVED  + " downloaded! \n(" + totalBytes + " bytes  / " + receivedBytes+ " bytes )" + " Ascii Armored: " +asciiArmor);
					System.out.println("\nCompleted in " + totalTime + " seconds");
					succession = true;
}
				else{
					System.out.println("Failed to received file! ( " +  totalBytes + " bytes downloaded / " + receivedBytes + " bytes expected)!\nAttempt: " + (p+1) + " (up to three tries)" );
					if(p==2)
						System.out.println("Terminating!");	
					succession = false;			
			}
			p++;
		}
		 
	  	}
	  	finally {
	  		//close everything used
	  		if (fos != null) fos.close();
	  		if (bos != null) bos.close();
	  		if (sock != null) sock.close();
	  	}
	  }

	}
