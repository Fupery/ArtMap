package me.Fupery.Artiste.Command.CanvasCommands;

import static me.Fupery.Artiste.Utils.Formatting.*;

public class Remove extends CanvasCommand {

	public void initialize() {
		usage = "remove";
		adminRequired = true;
		success = colourA + "Canvas removed successfully!";
	}

	public boolean run() {
		canvas.removeCanvas(sender);
		return true;
	}
}
