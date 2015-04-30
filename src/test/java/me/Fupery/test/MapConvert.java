package me.Fupery.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.Artwork;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.map.MapPalette;

import com.evilco.mc.nbt.stream.NbtInputStream;
import com.evilco.mc.nbt.stream.NbtOutputStream;
import com.evilco.mc.nbt.tag.ITag;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagByteArray;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagInteger;
import com.evilco.mc.nbt.tag.TagShort;

@Deprecated
public class MapConvert {

	private DyeColor[] map;
	private byte[] mapOutput;
	private short id;
	private int mapSize;
	private Logger log;
	private String name;

	public MapConvert(CommandSender sender, Artwork map) {

		this.map = map.getMap();
		id = map.getMapId();
		name = "map_" + id;
		mapSize = map.getMapSize();

		mapOutput = convert();
		File f = mapOverride();

		try {
			test(f);

			log.info("Map Save Successful!");
		} catch (Exception e) {
			log.info("Writing error " + e);
		}

	}

	private File mapOverride() {
		String mapFile = name + ".dat";
		Canvas c = StartClass.canvas;

		File wf = Bukkit.getWorld(c.worldname).getWorldFolder();
		File dir = new File(wf.getAbsolutePath() + File.separator + "data");
		File f = null;
		log.info("Searching for maps at " + dir.getAbsolutePath());
		for (String s : dir.list()) {
			if (s.equalsIgnoreCase(mapFile)) {
				log.info("File " + s.toString()
						+ " found. Overwriting file ...");
				f = new File(dir.getAbsolutePath(), s);
			}
		}
		if (f != null)
			f.delete();

		File file = new File(dir.getAbsolutePath(), mapFile);
		try {

			file.createNewFile();
			log.info("Creating new save file ...");
		} catch (IOException e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}

		return file;

	}

	private byte[] convert() {

		mapOutput = new byte[128 * 128];

		int i = 0;
		int s = mapSize;
		int f = 128 / s;

		// x & z represent position
		for (int x = 0; x < 128; x += f, i++) {

			for (int z = 0; z < 128; z += f, i++) {

				DyeColor d = this.map[i];

				byte col = colourConvert(d);
				// this for loop scales smaller maps up -
				// ix + iz represent displacement from x&y
				// to fill gaps by interpolating pixels
				for (int ix = x; ix < (x + f); ix++) {

					for (int iz = z; iz <= (z + f); iz++) {

						mapOutput[(ix + iz) * s] = col;

					}
				}
			}

		}
		return mapOutput;
	}

	private byte colourConvert(DyeColor color) {
		Color c = color.getColor();
		int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
		byte code = MapPalette.matchColor(r, g, b);
		return code;
	}

	public void test(File f) throws IOException {
		// create a hello world tag structure
		TagCompound compound = new TagCompound(name);

		TagCompound compound1 = new TagCompound("data");
		compound1.setTag(new TagByte("dimension", ((byte) 5)));
		compound1.setTag(new TagByte("scale", ((byte) 3)));
		compound1.setTag(new TagShort("height", ((short) 128)));
		compound1.setTag(new TagShort("width", ((short) 128)));
		compound1.setTag(new TagInteger("xCenter", 0));
		compound1.setTag(new TagInteger("zCenter", 0));
		compound1.setTag(new TagByteArray("colors", mapOutput));

		compound.setTag(compound1);

		// create output stream
		FileOutputStream outputStream = new FileOutputStream(f);
		NbtOutputStream nbtOutputStream = new NbtOutputStream(outputStream);

		// write data
		nbtOutputStream.write(compound);

		nbtOutputStream.flush();
		nbtOutputStream.close();

		FileInputStream inputStream = new FileInputStream(f);
		NbtInputStream nbtInputStream = new NbtInputStream(inputStream);

		// read data
		ITag tag = nbtInputStream.readTag();

		// verify output
		log.info(tag.getName());
		nbtInputStream.close();
	}

}
