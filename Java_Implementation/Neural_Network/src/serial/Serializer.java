package serial;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class Serializer {
	 
    public Serializer() {    }
     
    public void serialize(String path, Object obj) throws Exception {
    	FileOutputStream outFile = new FileOutputStream(path);
        ObjectOutputStream s = new ObjectOutputStream(outFile);
        s.writeObject(obj);
        s.close();
    } 
}