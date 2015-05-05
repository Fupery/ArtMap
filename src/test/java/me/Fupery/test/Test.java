package me.Fupery.test;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.Command.AbstractCommand;
import me.Fupery.Artiste.Tasks.Archive;

public class Test extends AbstractCommand {

	private DyeColor[] map;

	public Test(CommandListener listener) {

		super(listener);
	}

	protected boolean run() {

		new Archive().runTask(StartClass.plugin);
		return true;
	}

	@SuppressWarnings("deprecation")
	public void edit() {

		Canvas c = StartClass.canvas;

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

}
