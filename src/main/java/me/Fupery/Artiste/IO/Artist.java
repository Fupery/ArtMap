package me.Fupery.Artiste.IO;

import java.io.Serializable;
import java.util.UUID;

import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.Buffer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/** Serializable representation of the player's
 *  interaction with Artiste. Contains max number
 *  of artworks and a personal canvas buffer */
public class Artist implements Serializable{

	private static final long serialVersionUID = -2144942731017270259L;
	private UUID artistID;
	private Buffer buffer;
	private int artworks;
	private int maxArtworks;
	
	public Artist(UUID uuid){
		if(!StartClass.artistList.containsKey(uuid)){
			this.artistID = uuid;
			this.artworks = 0;
		}	
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
	public void clearBuffer(){
		this.buffer = null;
	}
	public boolean canPublish(){
		return (maxArtworks > artworks);
	}
	public void evalMaxArtworks(){
		Player p = Bukkit.getPlayer(artistID);
		
		if(p.hasPermission("artiste.playerTier1"))
			maxArtworks = StartClass.config.getInt("maxMaps.playerTier1");
		if(p.hasPermission("artiste.playerTier2"))
			maxArtworks = StartClass.config.getInt("maxMaps.playerTier2");
		if(p.hasPermission("artiste.staff"))
			maxArtworks = StartClass.config.getInt("maxMaps.admin");
	}
	public void increment(){
		this.artworks ++ ;
	}
	public void decrement(){
		this.artworks -- ;
	}

}
