package me.Fupery.Artiste.IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class Save {
	   public static void save(File saveFile, Object object) {
	    	
	    	try{
	    		if(!saveFile.exists()) saveFile.createNewFile();
	    		
	    		ObjectOutputStream out = new
	    		ObjectOutputStream(new FileOutputStream(saveFile));
	    		
	    		out.writeObject( (Object) object);
	    		
	    		out.flush();
	    		out.close();
	    		
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
}
