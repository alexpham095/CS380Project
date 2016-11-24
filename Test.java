package src;

import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException {
        String x = "hello my name is alex and i like to jump rope all the way down town";
        Key key = new Key();
        Encryption encrypt = new Encryption();
        Decryption decrypt = new Decryption();
       // FileSplit split = new FileSplit();
        byte[] mybytearray = args[1].getBytes();

        byte[] keyB = Key.keyGenerate(args[0]);
        byte[] enc = encrypt.encrypt(keyB, args[1]);
        byte[] dec = decrypt.decrypt(keyB, enc);
        
    }
}
