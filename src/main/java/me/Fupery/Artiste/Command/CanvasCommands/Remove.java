package me.Fupery.Artiste.Command.CanvasCommands;

public class Remove extends CanvasCommand {

	public void initialize() {
		
		usage = "remove";
		adminRequired = true;
	}

	public boolean run() {

		canvas.removeCanvas(sender);
		return true;
	}
}
