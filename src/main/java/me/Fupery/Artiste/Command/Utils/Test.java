package me.Fupery.Artiste.Command.Utils;

import org.bukkit.DyeColor;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.Tasks.SetCanvas;

public class Test extends AbstractCommand {

	public void initialize() {
		success = "test";
	}

	@Override
	public boolean run() {

		new SetCanvas(DyeColor.PINK).runTask(Artiste.plugin);

		return true;
	}
}
