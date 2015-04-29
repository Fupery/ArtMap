package me.Fupery.Artiste.Tasks;

import java.util.logging.Logger;

import me.Fupery.Artiste.StartClass;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.map.MapPalette;

/** Converts array of DyeColor values to byte for nbt storage */
public class ColourConvert {

	Logger log;

	public ColourConvert() {
		log = Bukkit.getLogger();
	}

	public byte[] byteConvert(DyeColor[] input, int mapSize) {

		byte[] mapOutput = new byte[128 * 128];

		if (mapSize == 0) {
			mapSize = StartClass.config.getInt("mapSize");
			log.info("no mapSize found!");
		}

		int i = 0;
		int f = 128 / mapSize;

		for (int x = 0; x < 128; x += f, i++) {

			for (int z = 0; z < 128; z += f, i++) {

				DyeColor d = input[i];

				byte col = colourConvert(d);

				for (int ix = x; ix < (x + f); ix++) {

					for (int iz = z; iz < (z + f); iz++) {

						mapOutput[ix + iz * 128] = col;

					}
				}
			}
		}
		return mapOutput;
	}

	public DyeColor[] dyeConvert(byte[] input, int mapSize) {

		DyeColor[] mapOutput = new DyeColor[(mapSize * mapSize) + mapSize - 1];

		if (mapSize == 0) {
			mapSize = StartClass.config.getInt("mapSize");
			log.info("no mapSize found!");
		}

		int i = 0;
		int f = 128 / mapSize;

		for (int x = 0; x < mapSize; x++, i += (f ^ 2)) {

			for (int z = 0; z < mapSize; z++, i += (f ^ 2)) {

				byte c = input[i];

				DyeColor col = colourRevert(c);

				mapOutput[x + z * mapSize] = col;

			}
		}
		return mapOutput;
	}

	@SuppressWarnings("deprecation")
	private byte colourConvert(DyeColor color) {
		Color c = color.getColor();
		int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
		byte code = MapPalette.matchColor(r, g, b);
		return code;
	}

	@SuppressWarnings("deprecation")
	private DyeColor colourRevert(byte color) {
		java.awt.Color c = MapPalette.getColor(color);
		int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
		Color bc = Color.fromRGB(r, g, b);
		DyeColor d = DyeColor.getByColor(bc);
		return d;
	}
}
