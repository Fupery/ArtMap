package me.Fupery.Artiste.IO;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import me.Fupery.Artiste.Artiste;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.MapArt.Buffer;
import me.Fupery.Artiste.MapArt.PrivateMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Serializable representation of the player's interaction with Artiste.
 * <p>
 * Contains max number of artworks and a personal canvas buffer
 */
public class Artist implements Serializable {

	private static final long serialVersionUID = -2144942731017270259L;
	private UUID artistID;
	private ArrayList<String> artworks;
	private boolean banned;

	public Artist(UUID uuid) {

		if (!Artiste.artistList.containsKey(uuid)) {
			this.artistID = uuid;
		}
		artworks = new ArrayList<String>();
	}

	private int maxArt() {

		Player p = Bukkit.getPlayer(artistID);
		int i = 0;

		if (p.hasPermission("artiste.playerTier1"))
			i = Artiste.config.getInt("maxMaps.playerTier1");

		if (p.hasPermission("artiste.playerTier2"))
			i = Artiste.config.getInt("maxMaps.playerTier2");

		if (p.hasPermission("artiste.admin"))
			i = Artiste.config.getInt("maxMaps.admin");

		return i;

	}

	public String getName() {
		return Bukkit.getPlayer(artistID).getName();
	}

	public ArrayList<String> getArtworks() {
		return artworks;
	}

	public boolean addArtwork(String title) {

		int priv = 0;

		for (String s : artworks) {

			Artwork a = Artiste.artList.get(s);

			if (a != null && a instanceof PrivateMap)

				priv++;
		}

		if (priv >= maxArt())

			return false;

		artworks.add(title);

		return true;
	}

	public boolean delArtwork(String title) {

		if (!artworks.remove(title))

			return false;

		return true;
	}

	public UUID getArtistID() {
		return artistID;
	}

	public void setArtistID(UUID artistID) {
		this.artistID = artistID;
	}

	public Buffer getBuffer() {

		try {

			return (Buffer) ArtIO.loadBuffer(this);

		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setBuffer(Buffer b) {

		try {

			ArtIO.saveBuffer(b, this);

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public boolean isBanned() {
		return banned;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
	}
}
