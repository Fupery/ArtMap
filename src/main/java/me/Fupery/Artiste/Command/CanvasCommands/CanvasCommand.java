package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.Command.Utils.AbstractCommand;

public abstract class CanvasCommand extends AbstractCommand {

	public CanvasCommand() {
		super();
		canvasRequired = true;
	}
}
