import java.io.*;
import java.nio.ByteBuffer;

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
      int sizeOfFiles = 256;                                //used to change size of chunks
      byte[] buffer = new byte[sizeOfFiles];
      try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
          String name = f.getName();
          int tmp = 0;
          while ((tmp = bis.read(buffer)) > 0) {
            String filePath = f.getCanonicalPath().substring(0,f.getCanonicalPath().lastIndexOf(File.separator)); //gets canonical path of original file
            //write each chunk of data into separate file with different number in name
              File newFile = new File(filePath + "/.temp/", name + "." + String.format("%03d", chunkCounter++));  //stores in a .temp file for orginaztion purposes
              try (FileOutputStream out = new FileOutputStream(newFile)) {
                out.write(buffer, 0, tmp);             //outputs chunks to files
            }
        }
    }
}


public static int hash(byte[] x){
    int result = 0;

    for(int i = 0; i < x.length; i++){
        result = (result*199 + x[i])%1000;
    }
    return result;

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

public int getChunkCounter(){
   return chunkCounter;
}


}
