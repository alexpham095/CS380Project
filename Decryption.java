import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

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
}
