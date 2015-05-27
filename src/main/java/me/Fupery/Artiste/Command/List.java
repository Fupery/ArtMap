package me.Fupery.Artiste.Command;

import java.util.ArrayList;
import java.util.Set;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.MapArt.AbstractMapArt.validMapType;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.MapArt.PublicMap;
import me.Fupery.Artiste.MapArt.Template;
import me.Fupery.Artiste.Command.Utils.AbstractCommand;
import me.Fupery.Artiste.Command.Utils.Error;
import static me.Fupery.Artiste.Utils.Formatting.*;

//fix errors for no canvas
public class List extends AbstractCommand {

	private validMapType type;
	private ArrayList<Artwork> list;
	private int pages, maxLines;
	String[] returnList;

	public void initialize() {

		maxArgs = 3;
		maxLines = 7;
		playerRequired = true;
		usage = "list <private|public> [pg]";
		returnList = new String[maxLines + 2];
	}

	@Override
	public String conditions() {

		if (Artiste.artList.isEmpty()) {
			error = String.format(Error.noArtwork, "");
		}
		for (int i = 1; i < args.length; i++) {

			if (isNumber(args[i])) {
				pages = Integer.parseInt(args[i]);

			} else if (resolveType(args[i]) != null) {
				type = resolveType(args[i]);
			}
		}
		if (type == null) {
			type = validMapType.PRIVATE;
		}

		if (!sort() || list.size() < 1) {
			error = String.format(Error.noArtwork, type.toString()
					.toLowerCase());
		}
		return error;
	}

	public boolean run() {

		int line;

		if ((pages * maxLines) < list.size()) {
			line = pages * maxLines;
		} else {
			line = 0;
			pages = 0;
		}
		returnList[0] = header();
		int i, k, l;
		l = (Integer) (pages + 1);

		for (i = line, k = 1; i < list.size() && i < (pages + maxLines); i++, k++) {

			Artwork a = list.get(i);
			String name = (a instanceof Template) ? "template" : artist
					.getName();
			String buys = (a instanceof PublicMap) ? ((PublicMap) a).getBuys()
					+ " buys" : null;
			returnList[k] = format(a.getTitle(), name, buys);

			if (list.size() > (maxLines + line)) {
				returnList[k + 1] = footer(l);
			}
		}
		for (String s : returnList) {
			if (s != null) {
				sender.sendMessage(s);
			}
		}
		return true;
	}

	private boolean sort() {

		if (Artiste.artList.size() == 0) {
			return false;
		}
		list = new ArrayList<Artwork>();

		switch (type) {

		case PRIVATE:
			if (artist.getArtworks().size() == 0) {
				return false;
			}
			for (String s : artist.getArtworks()) {
				list.add(Artiste.artList.get(s));
			}
			break;

		default:
			Set<String> keys = Artiste.artList.keySet();

			for (String s : keys) {
				Artwork a = Artiste.artList.get(s);
				if (a.getType() == type) {
					list.add(a);
				}
			}
			break;
		}
		return true;
	}

	private validMapType resolveType(String s) {

		switch (validMapType.valueOf(s)) {
		case PRIVATE:
			return validMapType.PRIVATE;
		case PUBLIC:
			return validMapType.PUBLIC;
		case TEMPLATE:
			return validMapType.TEMPLATE;
		case QUEUED:
			return validMapType.QUEUED;
		default:
			return null;
		}
	}

	private String header() {
		String s;
		if (type.name().equalsIgnoreCase("private")) {
			s = colourC + sender.getName() + "'s";
		} else {
			s = type.name().toLowerCase();
		}
		return colourA + String.format("Showing %s artworks", s + colourA);
	}

	private String footer(int l) {
		return String.format(colourD + "/artmap list %s %s[%s]%s for more",
				type.toString().toLowerCase(), colourE, l, colourB);
	}

	private String format(String title, String name, String buys) {
		String s = String.format("%s•  %s%s %sby %s%s ", colourA,
				evalColour(title), title, colourA, colourB, name);

		return (buys == null) ? s : s + colourD + buys;
	}

	private boolean isNumber(String s) {
		
		for (Character c : s.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}
}
