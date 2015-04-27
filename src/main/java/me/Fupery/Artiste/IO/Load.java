package me.Fupery.Artiste.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.AbstractMapArt;

public class Load {
	
	@SuppressWarnings("unchecked")
	public static void setupRegistry(StartClass plugin, Logger log){
		        
        File dir = plugin.getDataFolder();
        
        if(!dir.exists())
        	if(!dir.mkdir()) 
        		System.out.println("Error: Could not create directory");
        
        Object o;
        Object[] oList;
        
        o = load(new File(dir,"MapArt.dat"));

    	log.info("Loading MapArt.dat");
        if(o != null){
        	StartClass.artList = (HashMap<String, AbstractMapArt>) o;
        	
        }else{
            log.info("MapArt.dat not found ... creating new MapArt registry");
        	StartClass.artList = new HashMap<String, AbstractMapArt>();
        }
        
        
        log.info("Loading Artist.dat");
        o = load(new File(dir,"Artist.dat"));
        
        if(o != null){
        	StartClass.artistList = (HashMap<UUID, Artist>) o;
        	
        }else{
            log.info("Artist.dat not found ... creating new Artist registry");
        	StartClass.artistList = new HashMap<UUID, Artist>();
        }
        
        
        log.info("Loading Artiste.dat");
        oList = loadArray(new File(dir,"Artiste.dat"));
        
        if(oList != null){
        	
        	StartClass.canvas = (Canvas) oList[0];
        	StartClass.idList = (String[]) oList[1];
        	
        }else{
            log.info("Artiste.dat not found ... creating new canvas registry");
        	StartClass.canvas = null;
        	StartClass.idList = new String[50];
        }	
	}
	public static Object load(File loadFile){
    	try{
    		if(loadFile.exists()){
	    		
	    		ObjectInputStream in = new 
	    		ObjectInputStream(new FileInputStream(loadFile));
	    		Object object = in.readObject();
	    		in.close();
	    		return object;
    		} else return null;
    		
    	}catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
	public static Object[] loadArray(File loadFile){
    	try{
    		if(loadFile.exists()){
    			
    			Object[] o = new Object[2];
	    		
	    		ObjectInputStream in = new 
	    		ObjectInputStream(new FileInputStream(loadFile));
	    		
	    		o[0] = in.readObject();
	    		o[1] = in.readObject();
	    		
	    		in.close();
	    		return o;
    		} else return null;
    		
    	}catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
	
}
