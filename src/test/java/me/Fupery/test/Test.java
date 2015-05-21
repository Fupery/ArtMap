package me.Fupery.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Command.AbstractCommand;
import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.MapArt.Artwork;

public class Test extends AbstractCommand {

	private DyeColor[] map;
	private File data;

	public void initialize() {

		usage = "test <save|load>";
		success = "success!";
		maxArgs = 2;
		minArgs = 2;
		data = new File(Artiste.plugin.getDataFolder(), "data");
	}

	public boolean run() {

		switch (args[1]) {
		case "save":
			try {
				archiveArtList();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case "load":
			try {
				loadArtList();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		default : sender.sendMessage("error");
			return false;
		}

		return true;
	}

	@SuppressWarnings("deprecation")
	public void edit() {

		Canvas c = Artiste.canvas;

		if (c != null && map != null) {

			Location l = c.getPos1().clone();

			int i = 0;
			for (int x = c.getPos1().getBlockX(); x <= c.getPos2().getBlockX(); x++, i++) {

				for (int z = c.getPos1().getBlockZ(); z <= c.getPos2()
						.getBlockZ(); z++, i++) {

					l.setX(x);
					l.setZ(z);
					Block b = l.getBlock();

					if (map[i] != null) {

						DyeColor d = map[i];

						if (b.getType() != Material.WOOL)
							b.setType(Material.WOOL);
						if (b.getData() != d.getData())
							b.setData(d.getData());
					}
				}
			}
		}
	}
	public void archiveArtList() throws IOException {

		Set<String> keys = Artiste.artList.keySet();

		if (keys.size() == 0)
			return;

		AbstractMapArt art;
		String t = null;

		for (String key : keys) {

			art = Artiste.artList.get(key);

			if (art instanceof Artwork)

				t = ((Artwork) art).getTitle() + ".dat";

			if (t != null) {

				File f = new File(data, t);

				ObjectOutputStream out = new ObjectOutputStream(
						new GZIPOutputStream(new FileOutputStream(f, true)));

				out.writeObject(art);
				out.flush();
				out.close();
			}
		}
	}

	public boolean loadArtList() throws FileNotFoundException, IOException,
			ClassNotFoundException {

		if (!data.exists())

			return false;

		File[] files = data.listFiles();

		if (files.length == 0)

			return false;

		HashMap<String, Artwork> artList = new HashMap<String, Artwork>();

		for (File f : files) {

			if (f.getName().contains(".dat")) {

				Artiste.plugin.getLogger().info(f.getAbsolutePath());

				ObjectInputStream in = new ObjectInputStream(
						new GZIPInputStream(new FileInputStream(f)));

				Object o = in.readObject();

				if (o instanceof Artwork)

					artList.put(((Artwork) o).getTitle(), (Artwork) o);

				in.close();
			}
		}
		Artiste.artList = artList;
		return true;
	}



}
