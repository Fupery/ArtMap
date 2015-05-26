package me.Fupery.Artiste.Command.Utils;

import me.Fupery.Artiste.Canvas;
import me.Fupery.Artiste.IO.Artist;
import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.MapArt.AbstractMapArt.validMapType;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.Utils.Formatting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.Artiste.Artiste;

import static me.Fupery.Artiste.Command.Utils.Error.*;

/**
 * Framework for commands, flag precondition booleans etc. as true in
 * initialize().
 * <p>
 * Command body in run(), optionally add more conditions/checks with
 * conditions().
 */
public abstract class AbstractCommand implements ArtisteCommand {

	protected boolean playerRequired, canvasRequired, adminRequired,
			artistRequired, claimRequired, coolOffRequired, authorRequired,
			artRequired, disablePrefix;

	protected String usage, error, success, title, prefix;

	protected Artist artist;
	protected Canvas canvas;
	protected AbstractMapArt art;
	protected validMapType type;

	protected int minArgs = 1, maxArgs = 1;

	protected String[] args;
	protected CommandSender sender;

	protected AbstractCommand() {

		this.canvas = Artiste.canvas;
		prefix = Formatting.prefix;
	}

	public void check() {

		error = preconditions();

		if (error == null)

			error = conditions();

		if (error == null)

			if (run()) {

				success();
				return;
			}

		error();
	}

	public String preconditions() {

		if (sender instanceof Player) {

			artist = Artiste.artistList.get(((Player) sender).getUniqueId());

			if (artist == null) {

				artist = new Artist(((Player) sender).getUniqueId());

				Artiste.artistList.put(((Player) sender).getUniqueId(), artist);

			} else if (artistRequired && artist.isBanned())

				return error = "You have been banned from creating artworks.";
		}

		if (args.length < minArgs || args.length > maxArgs)

			return ChatColor.RED + "/artmap " + usage;

		if (playerRequired && !(sender instanceof Player))

			return noConsole;

		if (adminRequired && !(sender.hasPermission("artiste.admin")))

			return noPermission;

		if (canvasRequired) {

			if (canvas == null)

				return noDef;

			if (sender instanceof Player) {

				if (claimRequired && canvas.getOwner() != (Player) sender)

					return notOwner;
			} else

			if (canvas.getOwner() == null)

				return "The canvas has not been claimed!";

			if (coolOffRequired && canvas.isCoolingOff())

				return coolOff;
		}

		if (artRequired) {

			art = Artiste.artList.get(args[1]);

			if (art == null || art.getArtist() == null)

				return String.format(noMap, args[1]);

			type = art.getType();

			if (art instanceof Artwork)

				title = ((Artwork) art).getTitle();

			if (authorRequired
					&& sender instanceof Player
					&& art.getArtist().compareTo(
							((Player) sender).getUniqueId()) != 0)

				if (art instanceof Artwork)

					return noEdit;

			if (args.length > 1)
				title = args[1];
		}

		return null;
	}

	public String conditions() {
		return error;
	}

	protected void error() {

		String msg = (error == null) ? usage() : error;

		if (sender instanceof Player)

			sender.sendMessage(ChatColor.RED + msg);

		else

			Bukkit.getLogger().info(msg);
	}

	protected void success() {

		String s = (disablePrefix) ? success : prefix + success;

		if (success != null) {

			if (sender instanceof Player)

				sender.sendMessage(s);

			else

				Bukkit.getLogger().info(s);
		}
	}

	protected String usage() {

		String msg = (usage == null) ? "help for a full list of commands"
				: usage;

		return "/artmap " + msg;
	}

	public void pass(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
}
