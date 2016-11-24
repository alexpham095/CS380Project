/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;
import java.io.*;

/**
 *
 * @author alexpham
 */
public class Encryption {
	
	private static Key keyGenerator;
	private static byte[] key;
	
	public Encryption(File file, String key) throws IOException{
		keyGenerator = new Key(key);
		this.key = keyGenerator.getKey();
    	splitFile(file);
    }
    
    public static void splitFile(File f) throws IOException {
        int partCounter = 1;//I like to name parts from 001, 002, 003, ...
                            //you can change it to 0 if you want 000, 001, ...

        int sizeOfFiles = 1024 * 1024;// 1MB
        byte[] buffer = new byte[sizeOfFiles];

        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(f))) {
            String name = f.getName();

            int tmp = 0;
            while ((tmp = bis.read(buffer)) > 0) {
                //write each chunk of data into separate file with different number in name
                File newFile = new File(f.getParent(), name + "."
                        + String.format("%03d", partCounter++));
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                	
                    out.write(buffer, 0, tmp);//tmp is chunk size
                }
            }
        }
    }

    
    public static void hash(byte[] x){
        int result = 0;
        
        for(int i = 0; i < x.length; i++){
            result = (result*199 + x[i])%1000;
        }
        System.out.println("result is: " + result);
        
    }
    
    public static byte[] encrypt(String data) throws IOException{ //data = name of the file to send
    	RandomAccessFile in = new RandomAccessFile(data, "r");
    	byte [] input = new byte[(int) in.length()];	//input = actual data
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
