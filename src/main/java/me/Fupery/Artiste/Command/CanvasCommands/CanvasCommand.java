package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Command.Error;
import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.Command.AbstractCommand;

import org.bukkit.entity.Player;

public class CanvasCommand extends AbstractCommand {

	protected boolean claimRequired;
	protected boolean coolOffRequired;
	protected Canvas canvas;

	public CanvasCommand(CommandListener listener) {

		super(listener);
		canvasRequired = true;

	}

	protected String evaluate() {

		error = super.evaluate();

		if (error != null)
			
			return error;

		this.canvas = StartClass.canvas;

		if (canvas != null && sender instanceof Player) {

			if (claimRequired && canvas.getOwner() != (Player) sender)
				error = Error.notOwner;
		}
		if(coolOffRequired && canvas.isCoolingOff())
			error = Error.coolOff;

		return error;
	}

}
