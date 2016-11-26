import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Key {
	
	private RandomAccessFile in;
	
	public Key(String y) throws IOException{
		in = new RandomAccessFile(y, "r");
	}
	
	public byte[] getKey() throws IOException{
		byte [] input = new byte[(int) in.length()];
		in.readFully(input);
		for(int i = 0; i < (int) in.length(); i++){
			
			System.out.println((char) input[i]);
		}
		
		return input;
	}
}
