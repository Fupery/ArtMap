package me.Fupery.Artiste.Command;

import me.Fupery.Artiste.StartClass;
import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.MapArt.AbstractMapArt.validMapType;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.MapArt.PrivateMap;
import me.Fupery.Artiste.MapArt.PublicMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//TODO - config checks - publish system
public class MapHandler {
	
	private AbstractMapArt map;
	private CommandSender sender;
	private String[] args;
	
	public MapHandler(CommandSender sender, String[] args){
		
		this.sender = sender;
		
		if(args.length > 1)
			map = StartClass.artList.get(args[1]);
	}
	
public boolean save(CommandSender sender, String[] args){
		
		int t = args[1].length();
		
		if (t > 16 || t < 3 || args[1].equalsIgnoreCase("public")
		|| args[1].equalsIgnoreCase("private")) { 	
			
			sender.sendMessage(Error.invalSave);
			return false;
		}
		if (StartClass.artList.get(args[1]) != null) {
						
			sender.sendMessage(Error.alreadySaved);
			return false;
		} 
		new PrivateMap(sender, args[1]);
		return true;
	}

	public boolean publish() {
		
		 if(!mapFound(args)) return false;
		
		if(!sender.hasPermission("artiste.publicMapCreation")){
			
			sender.sendMessage(Error.noPubPermission);
			return false;
		}
		if(map.getType() == validMapType.PRIVATE)
			( (PrivateMap) map).setQueued(true);
		return true;
	}
	
	public boolean edit(){
		
		if(!mapFound(args)) return false;
		
		if(map instanceof Artwork){
			
			if(map.getArtist() != ((Player) sender).getUniqueId()){
				
				sender.sendMessage(Error.noEdit);
				return false;
			}
		sender.sendMessage(ChatColor.GOLD + "Loading " + ChatColor.AQUA +
		((Artwork) map).getTitle() + ChatColor.GOLD + "to the canvas");
		}
		map.edit();
		return true;
	}
	
	public void buy(){
		
		if(!mapFound(args)) return;
		
		if(map instanceof Artwork){
			
			Buy b = new Buy(sender);
			if(b.buy()){
				((Artwork) map).buy(sender);
			}
		}
	}

	public boolean delete() {
		
		if(!mapFound(args)) return false;
		
		if(sender instanceof Player){
			
			Player player = (Player) sender;
			
			if(player.getUniqueId() != map.getArtist() &&
			!(player.hasPermission("artiste.override")) )
				return false;
		}
		if(map instanceof Artwork){
			
			((Artwork) map).delete(sender);
			return true;
		}
		return false;
	}
	
	public boolean approve() {
		if(!mapFound(args)) return false;
		
		if(map.getType() == validMapType.PRIVATE_QUEUED){
			new PublicMap( sender, ((PrivateMap) map) );
			return true;
		}
		sender.sendMessage(String.format(Error.notQueued, args[2]));
		return false;
	}
	
	private boolean mapFound(String[] args){
			
		if(map == null){
			if(args.length > 2)
				sender.sendMessage(String.format(Error.noMap, args[2]) );
			else
				sender.sendMessage(Error.usage(args[0]));
			return false;
		}
		return true;
	}
}
