package me.Fupery.Artiste.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.Artwork;

public class ArtIO {
	
	private File file;
	private StartClass plugin;
	
	public ArtIO(){
		
		plugin = StartClass.plugin;
		File dir = plugin.getDataFolder();
		this.file = (new File(dir, "Art.dat"));
		
	}

	public void archiveArtList() {

		Set<String> keys = StartClass.artList.keySet();

		try {
			if (!file.exists())
				file.createNewFile();

			ObjectOutputStream out = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(file)));

			for (String k : keys)

				out.writeObject(StartClass.artList.get(k));

			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean loadMap(String title)
			throws FileNotFoundException, IOException {
		
		if(!file.exists())
			
			return false;

		ObjectInputStream in = null;
		Object o;

		in = new ObjectInputStream(new GZIPInputStream(
				new FileInputStream(file)));

		try {
			while (true) {

				o = in.readObject();

				if (o instanceof Artwork
						&& ((Artwork) o).getTitle().equalsIgnoreCase(title));
				break;
			}

		} catch (ClassNotFoundException | IOException e) {

			o = null;

			e.printStackTrace();
			
			return false;
		}

		in.close();
		Artwork a = (Artwork) o;
		
		StartClass.artList.put(a.getTitle(), a);
		
		return true;

	}

}
