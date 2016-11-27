
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Decryption {

	private static Key keyGenerator;
	private static byte[] key;

	public Decryption(String key) throws IOException{
		keyGenerator = new Key(key);          //inits key
		this.key = keyGenerator.getKey();     //stores key.txt as byte array
	}
	public static byte[] decrypt(byte[] data){
       byte[] result = data; 
       int i = 0, j =0;
       while(i != data.length){
        result[i] = (byte)(key[j] ^ data[i]);
        i++;
        j++;
        if(j == key.length){
            j = 0;
        }      
    }

    return result;

	}
	
	public static byte[] hash(byte[] x){
	    int result = 0;
	
	    for(int i = 0; i < x.length; i++){
	        result = (result*199 + x[i])%1000;
	    }
	    byte[] bytes = ByteBuffer.allocate(4).putInt(result).array();
	    return bytes;
	
	}
}