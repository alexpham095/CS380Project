package src;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Decryption {

	public static byte[] decrypt(byte[] key, String data) throws IOException{
		RandomAccessFile in = new RandomAccessFile(data, "r");
    	byte [] input = new byte[(int) in.length()];
		in.readFully(input);
		
        byte[] result = new byte[input.length];
        int i = 0, j =0;
        while(i != input.length){
            result[i] = (byte)(key[j] ^ input[i]);
            i++;
            j++;
            if(j == key.length){
                j = 0;
            }      
        }
        
       return result;
        
    }
}
