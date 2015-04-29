package me.Fupery.Artiste.IO;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.Buffer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Serializable representation of the player's interaction with Artiste.
 * Contains max number of artworks and a personal canvas buffer
 */
public class Artist implements Serializable {

	private static final long serialVersionUID = -2144942731017270259L;
	private UUID artistID;
	private Buffer buffer;
	private String[] artworks;

	public Artist(UUID uuid) {
		if (!StartClass.artistList.containsKey(uuid)) {
			this.artistID = uuid;
			evalMaxArtworks();
		}
	}
	public void evalMaxArtworks() {
		Player p = Bukkit.getPlayer(artistID);
		int i = 0;

		if (p.hasPermission("artiste.playerTier1"))
			i = StartClass.config.getInt("maxMaps.playerTier1");

		if (p.hasPermission("artiste.playerTier2"))
			i = StartClass.config.getInt("maxMaps.playerTier2");

		if (p.hasPermission("artiste.staff"))
			i = StartClass.config.getInt("maxMaps.admin");

		if (artworks == null)
			artworks = new String[i];
		
		if(artworks.length != i)
			
			artworks = Arrays.copyOf(artworks, i);

	}

	public boolean addArtwork(String title) {

		if (artworks != null)

			for (int i = 0; i < artworks.length; i++)

				if (artworks[i] == null) {

					artworks[i] = title;
					return true;
				}
		return false;
	}

	public boolean delArtwork(String title) {

		if (artworks != null)

			for (int i = 0; i < artworks.length; i++)

				if (artworks[i] == title) {

					artworks[i] = null;
					return true;
				}
		return false;
	}

	public UUID getArtistID() {
		return artistID;
	}

	public void setArtistID(UUID artistID) {
		this.artistID = artistID;
	}

	public Buffer getBuffer() {
		return buffer;
	}

	public void setBuffer(Buffer buffer) {
		this.buffer = buffer;
	}

	public void clearBuffer() {
		this.buffer = null;
	}
}
