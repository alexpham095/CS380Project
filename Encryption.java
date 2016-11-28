/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.nio.ByteBuffer;


/**
 *
 * @author alexpham
 */
public class Encryption {
	
	private static Key keyGenerator;
	private static byte[] key;
	private static int chunkCounter;
	
	public Encryption(File file, String key) throws IOException{
        keyGenerator = new Key(key);            //inits key
        this.key = keyGenerator.getKey();       //stores key.txt as byte array
        chunkCounter = 0;                       //global counter of how many chunks there should be
        splitFile(file);                        //splits file into schunks
    }
    
    public static void splitFile(File f) throws IOException {
      int sizeOfFiles = 5000;                                //used to change size of chunks
      
	int l = 0;
      try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
    	  String fileSize = Long.toString(f.length());		//filesize
    	  byte[] buffer = new byte[sizeOfFiles];
          String name = f.getName();
          int tmp = 0;
          while ((tmp = bis.read(buffer)) > 0) {
            String filePath = f.getCanonicalPath().substring(0,f.getCanonicalPath().lastIndexOf(File.separator)); //gets canonical path of original file
            //write each chunk of data into separate file with different number in name
              File newFile = new File(filePath + "/.temp/", name + "." + String.format("%03d", chunkCounter++));  //stores in a .temp file for orginaztion purposes
 	   
              try (FileOutputStream out = new FileOutputStream(newFile)) { 
		byte[] ph = new byte[tmp];
		for(int i = 0; i<tmp; i++){
			ph[i] = buffer[i];
		}
		String hash = new String(hash(ph));		
		String head = Integer.toString(tmp) + "%%" + fileSize + "%%" + hash + "%%";		//string to concat and has the chunk size 
    	 	byte[] header = head.getBytes();

	//	System.out.println("Chunk with header size: " + (header.length+tmp));
		out.write(header,0,header.length);
                out.write(buffer, 0, tmp);             //outputs chunks to files
	            }
	        }
	    }
	}


	public static byte[] hash(byte[] x){
	    int result = 0;
	
	    for(int i = 0; i < x.length; i++){
	        result = (result*199 + x[i])%1000;
	    }
	    byte[] bytes = ByteBuffer.allocate(4).putInt(result).array();
		String hashed = new String(bytes);
		//System.out.println(hashed);
	    return bytes;
	
	}
	
	public static byte[] encryptHash(int x) throws IOException{ //data = name of the file to send
	   byte[] bytes = ByteBuffer.allocate(4).putInt(x).array();
	   byte[] result = new byte[bytes.length];
	   int i = 0, j =0;
	   while(i != bytes.length){
		   result[i] = (byte)(key[j] ^ bytes[i]);
		   i++;
		   j++;
		   if(j == key.length){
			   j = 0;
		   }      
	  }
	
	  return result;
	
	}

    public static byte[] encryptChunk(byte[] data) throws IOException{ //data = name of the file to send
        byte[] result = new byte[data.length];
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

	public byte[] change(String data) throws IOException{         //changes file to byte array
		RandomAccessFile in = new RandomAccessFile(data, "r");
		byte[] input = new byte[(int) in.length()];
		in.readFully(input);
		return input;
	}
	
	public static byte[] changeHash(File f) throws IOException{
		RandomAccessFile in = new RandomAccessFile(f, "r");
   	  	byte[] input = new byte[(int) in.length()];
   	  	in.readFully(input);
   	  	return input;
	}

	public int getChunkCounter(){
	   return chunkCounter;
	}

	public static byte[] asciiEncode(byte[] ba) {
		int[] c = new int[((ba.length/3)*24)+24];
		String six = "";
		String encoded = "";
		int counter = 0;
		int bcounter = 0;
	
		boolean check = false;
		if(ba.length ==0 ){
			check = true;
		}
		for(int i = 0; i <= ba.length/3; i++){
		
		for(int j = 0; j < 8; j++){
			if(bcounter < (ba.length) && check == false){

			c[counter] = ba[bcounter]>>(8-(j+1)) &0x0001;
			}
			else if(check == true){
				
				c[counter] = 2;
			}
			else if(j == 7){
				check = true;
				c[counter] = 2;
			}
			else{
				c[counter] = 0;
				if(j == 0){
					c[counter] = 2;	
				}
			}
		counter++;
		
		}
		for(int k = 8; k < 16; k++){
			if(bcounter +1 < ba.length  && check == false){
			c[counter] = ba[bcounter+1]>>(8-((k%8)+1)) &0x0001;
			
			}
			else if(check == true){
				c[counter] = 2;
				
			}
			else if(k == 15){
				check = true;
				c[counter] =2;
				
			}
			else{
				c[counter] = 0;
				if(k == 0){
					c[counter] = 2;	
				}
			}
			
		counter++;
		}
		for(int l = 16; l < 24; l++){
			if(bcounter+2 < (ba.length )  && check == false){
			c[counter] = ba[bcounter+2]>>(8-((l%8)+1)) &0x0001;
			}
			else if(check == true){
				c[counter] = 2;
			}
			else if(l == 23){
				check = true;
				c[counter] = 2;
			}
			else{
				c[counter] = 0;
				if(l == 0){
					c[counter] = 2;	
				}
			}
		counter++;
		}
	bcounter = bcounter + 3;
		}
		
		
		
		int sixCounter = 0;

		for(int i = 0; i < c.length;i++){
			
		
			six = six + Integer.toString(c[i]);
			sixCounter++;
			
			if (sixCounter%6 ==0){
		
				switch(six){
					case "000000": encoded = encoded + "A";
					break;	
					case "000001": encoded = encoded + "B";
					break;
					case "000010": encoded = encoded + "C";
					break;
					case "000011": encoded = encoded + "D";
					break;
					case "000100": encoded = encoded + "E";
					break;
					case "000101": encoded = encoded + "F";
					break;
					case "000110": encoded = encoded + "G";
					break;
					case "000111": encoded = encoded + "H";
					break;
					case "001000": encoded = encoded + "I";
					break;	
					case "001001": encoded = encoded + "J";
					break;
					case "001010": encoded = encoded + "K";
					break;
					case "001011": encoded = encoded + "L";
					break;
					case "001100": encoded = encoded + "M";
					break;
					case "001101": encoded = encoded + "N";
					break;
					case "001110": encoded = encoded + "O";
					break;
					case "001111": encoded = encoded + "P";
					break;
					case "010000": encoded = encoded + "Q";
					break;	
					case "010001": encoded = encoded + "R";
					break;
					case "010010": encoded = encoded + "S";
					break;
					case "010011": encoded = encoded + "T";
					break;
					case "010100": encoded = encoded + "U";
					break;
					case "010101": encoded = encoded + "V";
					break;
					case "010110": encoded = encoded + "W";
					break;
					case "010111": encoded = encoded + "X";
					break;
					case "011000": encoded = encoded + "Y";
					break;	
					case "011001": encoded = encoded + "Z";
					break;
					case "011010": encoded = encoded + "a";
					break;
					case "011011": encoded = encoded + "b";
					break;
					case "011100": encoded = encoded + "v";
					break;
					case "011101": encoded = encoded + "d";
					break;
					case "011110": encoded = encoded + "e";
					break;
					case "011111": encoded = encoded + "f";
					break;
					case "100000": encoded = encoded + "g";
					break;	
					case "100001": encoded = encoded + "h";
					break;
					case "100010": encoded = encoded + "i";
					break;
					case "100011": encoded = encoded + "j";
					break;
					case "100100": encoded = encoded + "k";
					break;
					case "100101": encoded = encoded + "l";
					break;
					case "100110": encoded = encoded + "m";
					break;
					case "100111": encoded = encoded + "n";
					break;
					case "101000": encoded = encoded + "o";
					break;	
					case "101001": encoded = encoded + "p";
					break;
					case "101010": encoded = encoded + "q";
					break;
					case "101011": encoded = encoded + "r";
					break;
					case "101100": encoded = encoded + "s";
					break;
					case "101101": encoded = encoded + "t";
					break;
					case "101110": encoded = encoded + "u";
					break;
					case "101111": encoded = encoded + "v";
					break;
					case "110000": encoded = encoded + "s";
					break;	
					case "110001": encoded = encoded + "x";
					break;
					case "110010": encoded = encoded + "y";
					break;
					case "110011": encoded = encoded + "z";
					break;
					case "110100": encoded = encoded + "0";
					break;
					case "110101": encoded = encoded + "1";
					break;
					case "110110": encoded = encoded + "2";
					break;
					case "110111": encoded = encoded + "3";
					break;
					case "111000": encoded = encoded + "4";
					break;	
					case "111001": encoded = encoded + "5";
					break;
					case "111010": encoded = encoded + "6";
					break;
					case "111011": encoded = encoded + "7";
					break;
					case "111100": encoded = encoded + "8";
					break;
					case "111101": encoded = encoded + "9";
					break;
					case "111110": encoded = encoded + "+";
					break;
					case "111111": encoded = encoded + "/";
					break;
				default: encoded = encoded + "=";
				break;

				}
				six = "";
	
			}
		}
		return encoded.getBytes();
		}
	
	
	}
