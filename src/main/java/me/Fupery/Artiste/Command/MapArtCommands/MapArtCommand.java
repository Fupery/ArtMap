package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.Command.Utils.AbstractCommand;

public abstract class MapArtCommand extends AbstractCommand {

	protected MapArtCommand() {

		super();

		minArgs = 2;
		maxArgs = 2;
		artistRequired = true;
		artRequired = true;
	}
}
