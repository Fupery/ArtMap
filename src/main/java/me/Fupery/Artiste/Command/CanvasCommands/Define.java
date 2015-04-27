package me.Fupery.Artiste.Command.CanvasCommands;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.Command.Error;
import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;

public class Define extends CanvasCommand {

	public Define(CommandListener listener) {

		super(listener);
		usage = "define <x><y><z>";

		canvasRequired = false;
		adminRequired = true;
		minArgs = 4;
		maxArgs = 4;
	}

	protected String evaluate() {

		error = super.evaluate();

		if (StartClass.canvas != null)
			error = Error.alreadyDef;

		return error;
	}

	protected boolean run() {

		double p1, p2, p3;

		try {
			p1 = Double.parseDouble(args[1]);
			p2 = Double.parseDouble(args[2]);
			p3 = Double.parseDouble(args[3]);

		} catch (NumberFormatException e) {

			error = Error.define;
			return false;
		}

		Canvas.defineCanvas(sender, p1, p2, p3);
		return true;
	}

}
