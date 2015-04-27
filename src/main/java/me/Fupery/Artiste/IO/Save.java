package me.Fupery.Artiste.IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import me.Fupery.Artiste.Canvas;

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
	   
	   public static void save(File saveFile, Canvas c, String[] id) {
	    	
	    	try{
	    		if(!saveFile.exists()) saveFile.createNewFile();
	    		
	    		ObjectOutputStream out = new
	    		ObjectOutputStream(new FileOutputStream(saveFile));
	    		
	    		out.writeObject( (Object) c);
	    		out.writeObject( (Object) id);
	    		
	    		out.flush();
	    		out.close();
	    		
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
}
