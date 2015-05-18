package me.Fupery.Artiste.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.MapArt.Template;

public class Load {

	public static void setupRegistry(Artiste plugin, Logger log) {

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
			Artiste.artistList = new HashMap<UUID, Artist>();
			Artiste.artList = new HashMap<String, AbstractMapArt>();

			setupDefault();
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean load(File loadFile) {

		ObjectInputStream in;

		try {
			if (loadFile.exists()) {

				in = new ObjectInputStream(new GZIPInputStream(
						new FileInputStream(loadFile)));

				Artiste.canvas = (Canvas) in.readObject();
				Artiste.artistList = (HashMap<UUID, Artist>) in.readObject();
				Artiste.artList = (HashMap<String, AbstractMapArt>) in
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

	public static Template setupDefault() {

		if (Artiste.canvas == null)
			return null;

		File dir = Artiste.plugin.getDataFolder();
		File artDir = new File(dir, "data");

		Logger log = Bukkit.getLogger();

		File defaultMap = new File(artDir, "default.dat");

		if (defaultMap.exists())
			return null;

		InputStream in = new Load().getClass().getResourceAsStream(
				"/resources/default.dat");

		Object o;

		try {

			ObjectInputStream ob = new ObjectInputStream(
					new GZIPInputStream(in));

			o = ob.readObject();
			ob.close();

		} catch (IOException | ClassNotFoundException e) {

			o = null;
			e.printStackTrace();
		}
		DyeColor[] map = (DyeColor[]) o;

		Template t = new Template("default", map);

		Artiste.artList.put("default", t);

		log.info(Artiste.artList.get("default").toString());

		return t;
	}
}
