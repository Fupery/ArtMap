package me.Fupery.Artiste.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.AbstractMapArt;

public class Load {

	public static void setupRegistry(StartClass plugin, Logger log) {

		File dir = plugin.getDataFolder();
		File artDir = new File(dir, "data");

		if (!dir.exists())

			if (!dir.mkdir())
				System.out.println("Error: Could not create plugin directory");

		if (!artDir.exists())

			artDir.mkdir();

		log.info("Loading Artiste.dat");

		if (!load(new File(dir, "Artiste.dat"))) {

			log.info("Artiste.dat not found ... creating new Artist registry");
			StartClass.artistList = new HashMap<UUID, Artist>();
			StartClass.artList = new HashMap<String, AbstractMapArt>();
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean load(File loadFile) {

		try {
			if (loadFile.exists()) {

				ObjectInputStream in = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(loadFile)));

				StartClass.canvas = (Canvas) in.readObject();
				StartClass.recyclingBin = (ArrayList<Short>) in.readObject();
				StartClass.artistList = (HashMap<UUID, Artist>) in.readObject();
				StartClass.artList = (HashMap<String, AbstractMapArt>) in
						.readObject();

				in.close();
				return true;

			} else
				return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
