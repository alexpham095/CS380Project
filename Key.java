package encryption;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Key {
	
	public static byte[] keyGenerate(String y) throws IOException{
		RandomAccessFile in = new RandomAccessFile(y, "r");

		byte [] input = new byte[(int) in.length()];
		in.readFully(input);
		for(int i = 0; i < (int) in.length(); i++){
			
			System.out.println((char) input[i]);
		}
		
		return input;
	}
}
