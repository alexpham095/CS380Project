/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;
import java.io.*;
/**
 *
 * @author alexpham
 */
public class Encryption {

    
    public static void hash(byte[] x){
        int result = 0;
        
        for(int i = 0; i < x.length; i++){
            result = (result*199 + x[i])%1000;
        }
        System.out.println("result is: " + result);
        
    }
    
    public static byte[] encrypt(byte[] key, String data) throws IOException{
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
