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

	public static byte[] asciiDecode(byte[] e){
		String encoded = new String(e);
		int count = (encoded.length()) ;
		String one;
		String two; 
		String three;
		String four;
		String binary = "";
		String current = null;
		String total = "";
		for(int i = 0; i <= count; i++){
			if(encoded.length()-1 >=i)
				one = Character.toString(encoded.charAt(i));
			else 
				one = "";
			if(encoded.length()-1 >=i+1)
				two = Character.toString(encoded.charAt(++i));
			else 
				two = "";
			if(encoded.length()-1 >=i+2)
			three = Character.toString(encoded.charAt(++i));
			else 
				three = "";
				if(encoded.length()-1 >=i+3)
			four = Character.toString(encoded.charAt(++i));
			else 
				four = "";
			for(int j = 0; j < 4; j++){
				switch(j){
				case 0: current = one;
				break;
				case 1: current = two;
				break;
				case 2: current = three;
				break;
				case 3: current = four;
				break;
				default: break;
				}
						
			switch(current){
			case "A": binary = binary + "000000";
			break;	
			case "B": binary = binary + "000001";
			break;	
			case "C": binary = binary + "000010";
			break;	
			case "D": binary = binary + "000011";
			break;	
			case "E": binary = binary + "000100";
			break;	
			case "F": binary = binary + "000101";
			break;	
			case "G": binary = binary + "000110";
			break;	
			case "H": binary = binary + "000111";
			break;	
			case "I": binary = binary + "001000";
			break;	
			case "J": binary = binary + "001001";
			break;	
			case "K": binary = binary + "001010";
			break;	
			case "L": binary = binary + "001011";
			break;	
			case "M": binary = binary + "001100";
			break;	
			case "N": binary = binary + "001101";
			break;	
			case "O": binary = binary + "001110";
			break;	
			case "P": binary = binary + "001111";
			break;		
			case "Q": binary = binary + "010000";
			break;	
			case "R": binary = binary + "010001";
			break;	
			case "S": binary = binary + "010010";
			break;	
			case "T": binary = binary + "010011";
			break;	
			case "U": binary = binary + "010100";
			break;	
			case "V": binary = binary + "010101";
			break;	
			case "W": binary = binary + "010110";
			break;	
			case "X": binary = binary + "010111";
			break;	
			case "Y": binary = binary + "011000";
			break;	
			case "Z": binary = binary + "011001";
			break;	
			case "a": binary = binary + "011010";
			break;	
			case "b": binary = binary + "011011";
			break;	
			case "c": binary = binary + "011100";
			break;	
			case "d": binary = binary + "011101";
			break;	
			case "e": binary = binary + "011110";
			break;	
			case "f": binary = binary + "011111";
			break;
			case "g": binary = binary + "100000";
			break;	
			case "h": binary = binary + "100001";
			break;	
			case "i": binary = binary + "100010";
			break;	
			case "j": binary = binary + "100011";
			break;	
			case "k": binary = binary + "100100";
			break;	
			case "l": binary = binary + "100101";
			break;	
			case "m": binary = binary + "100110";
			break;	
			case "n": binary = binary + "100111";
			break;	
			case "o": binary = binary + "101000";
			break;	
			case "p": binary = binary + "101001";
			break;	
			case "q": binary = binary + "101010";
			break;	
			case "r": binary = binary + "101011";
			break;	
			case "s": binary = binary + "101100";
			break;	
			case "t": binary = binary + "101101";
			break;	
			case "u": binary = binary + "101110";
			break;	
			case "v": binary = binary + "101111";
			break;		
			case "w": binary = binary + "110000";
			break;	
			case "x": binary = binary + "110001";
			break;	
			case "y": binary = binary + "110010";
			break;	
			case "z": binary = binary + "110011";
			break;	
			case "0": binary = binary + "110100";
			break;	
			case "1": binary = binary + "110101";
			break;	
			case "2": binary = binary + "110110";
			break;	
			case "3": binary = binary + "110111";
			break;	
			case "4": binary = binary + "111000";
			break;	
			case "5": binary = binary + "111001";
			break;	
			case "6": binary = binary + "111010";
			break;	
			case "7": binary = binary + "111011";
			break;	
			case "8": binary = binary + "111100";
			break;	
			case "9": binary = binary + "111101";
			break;	
			case "+": binary = binary + "111110";
			break;	
			case "/": binary = binary + "111111";
			break;
			case "=": 				 
					  break;		
		default: ;
		break;	
		}	
			}	
			int length = binary.length() - binary.length()%8;
			binary = binary.substring(0, length);
			total = total + binary;
			binary = "";
		}
		byte[] ret = new byte[total.length()/8];
		for(int i = 0; i < ret.length; i++){
			ret[i] = (byte) Integer.parseInt(total.substring(i * 8, (i * 8) + 8), 2);
			
		}	
		
		return ret;
		
	}
}
