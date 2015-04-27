package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.CommandListener;

public class Remove extends CanvasCommand {

	public Remove(CommandListener listener) {

		super(listener);
		usage = "remove";
		adminRequired = true;

	}

	protected boolean run() {

		canvas.removeCanvas(sender);
		return true;
	}

}
