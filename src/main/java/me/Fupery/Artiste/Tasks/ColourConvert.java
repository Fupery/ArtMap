package me.Fupery.Artiste.Tasks;

import java.util.HashMap;

import me.Fupery.Artiste.Artiste;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.map.MapPalette;

/** Converts array of DyeColor values to byte for nbt storage */
public class ColourConvert {

	public static final HashMap<DyeColor, Byte> colourChart = setupColourChart();

	@SuppressWarnings("deprecation")
	public static HashMap<DyeColor, Byte> setupColourChart() {
		HashMap<DyeColor, Byte> chart = new HashMap<DyeColor, Byte>();
		chart.put(null, (byte) 0);
		for (DyeColor d : DyeColor.values()) {
			Color c = d.getColor();
			int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
			byte code = MapPalette.matchColor(r, g, b);
			chart.put(d, code);
		}
		return chart;
	}

	public static byte[] byteConvert(DyeColor[] input, int mapSize) {

		byte[] mapOutput = new byte[128 * 128];
		if (mapSize == 0) {
			mapSize = Artiste.config.getInt("mapSize");
			Bukkit.getLogger().info("no mapSize found!");
		}
		int i = 0;
		int f = 128 / mapSize;

		for (int x = 0; x < 128; x += f, i++) {
			for (int z = 0; z < 128; z += f, i++) {

				byte col = colourChart.get(input[i]);

				for (int ix = x; ix < (x + f); ix++) {
					for (int iz = z; iz < (z + f); iz++) {

						mapOutput[ix + iz * 128] = col;
					}
				}
			}
		}
		return mapOutput;
	}
}
