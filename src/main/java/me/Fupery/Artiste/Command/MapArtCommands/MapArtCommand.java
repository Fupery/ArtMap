package me.Fupery.Artiste.Command.MapArtCommands;

import me.Fupery.Artiste.CommandListener;
import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.Command.Error;
import me.Fupery.Artiste.Command.AbstractCommand;
import me.Fupery.Artiste.IO.Artist;
import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.MapArt.AbstractMapArt.validMapType;
import me.Fupery.Artiste.MapArt.Artwork;

import org.bukkit.entity.Player;

public class MapArtCommand extends AbstractCommand {

	protected String title;
	protected Artist artist;
	protected AbstractMapArt art;
	protected validMapType type;
	protected boolean authorRequired;

	protected MapArtCommand(CommandListener listener) {

		super(listener);
		minArgs = 2;
		maxArgs = 2;
		artistRequired = true;
	}

	protected String evaluate() {
		
		error = super.evaluate();

		if (error != null)
			
			return error;

		art = StartClass.artList.get(args[1]);

		if (art == null)

			return String.format(Error.noMap, args[1]);

		type = art.getType();

		if (authorRequired && sender instanceof Player
				&& art.getArtist() != ((Player) sender).getUniqueId())

			if (art instanceof Artwork)

			return Error.noEdit;

		return error;
	}
}
