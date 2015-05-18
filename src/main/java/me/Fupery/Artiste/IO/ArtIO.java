package me.Fupery.Artiste.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.DyeColor;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.MapArt.Buffer;

public class ArtIO {

	static File data = new File(Artiste.plugin.getDataFolder(), "data");
	static File buffer = new File(Artiste.plugin.getDataFolder(), "buffer");

	public static boolean saveMap(DyeColor[] map, String title)
			throws FileNotFoundException, IOException {

		if (!data.exists())

			data.mkdir();

		File f = new File(data, title + ".dat");

		if (f.exists())
			f.delete();

		ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(
				new FileOutputStream(f, true)));

		out.writeObject(map);
		out.flush();
		out.close();

		return true;
	}

	public static DyeColor[] loadMap(String title)
			throws ClassNotFoundException, IOException {

		if (!data.exists())

			return null;

		File b = null;

		for (File f : data.listFiles())

			if (f.getName().equalsIgnoreCase(title + ".dat")) {
				b = f;
				break;
			}

		if (b == null)

			return null;

		ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(
				new FileInputStream(b)));

		Object o = in.readObject();

		in.close();

		DyeColor[] map = (DyeColor[]) o;

		return map;
	}

	public static boolean deleteMap(String title) {

		if (!data.exists())

			return false;

		File f = new File(data, title + ".dat");

		return f.delete();
	}

	public static boolean saveBuffer(Buffer art, Artist a)
			throws FileNotFoundException, IOException {

		if (!buffer.exists())

			buffer.mkdir();

		File f = new File(buffer, a.getArtistID() + ".dat");

		if (f.exists())
			f.delete();

		ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(
				new FileOutputStream(f, true)));

		out.writeObject(art);
		out.flush();
		out.close();

		return true;
	}

	public static Buffer loadBuffer(Artist a) throws ClassNotFoundException,
			IOException {

		if (!buffer.exists())

			return null;

		File b = null;

		for (File f : buffer.listFiles())

			if (f.getName().contains(a.getArtistID().toString())) {
				b = f;
				break;
			}

		if (b == null)

			return null;

		ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(
				new FileInputStream(b)));

		Object o = in.readObject();

		in.close();

		Buffer art = (Buffer) o;

		if (art.getMapSize() == Artiste.canvas.getSize())

			return art;
		else {
			b.delete();
			return null;
		}
	}
	public static DyeColor[] loadResource(String title)
			throws ClassNotFoundException, IOException {

		if (!data.exists())

			return null;

		File b = null;

		for (File f : data.listFiles())

			if (f.getName().equalsIgnoreCase(title + ".dat")) {
				b = f;
				break;
			}

		if (b == null)

			return null;

		ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(
				new FileInputStream(b)));

		Object o = in.readObject();

		in.close();

		DyeColor[] map = (DyeColor[]) o;

		return map;
	}
}
