package me.Fupery.Artiste.Command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import me.Fupery.Artiste.MapArt.AbstractMapArt;
import me.Fupery.Artiste.MapArt.AbstractMapArt.validMapType;
import me.Fupery.Artiste.MapArt.Artwork;
import me.Fupery.Artiste.StartClass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class List {
	
	private CommandSender sender;
	private validMapType type;
	private Set<String> keys;
	private ArrayList<AbstractMapArt> list;
	
	public static void initialize(CommandSender sender, String args[]) {
		
		int pages;
		validMapType type;
		
		if(args.length == 3){
			try {
				pages = Integer.parseInt(args[2]);
			} catch (NumberFormatException e){
				pages = 0;
			}
		}
		else pages = 0;
		
		type = resolveType(args);
		
		if(!(StartClass.artList.isEmpty() ) ){
				
			Set<String> keys = StartClass.artList.keySet();
			new List(sender, keys, type, pages);
			keys = null;
		}else
		sender.sendMessage(Error.noArtwork);
	}
		
	public List(CommandSender sender,Set<String> keys , validMapType type, int lineNumber) {
		this.sender = sender;
		this.keys = keys;
		this.type = type;
		
		if(sort()){
			if(list.size() > 0 ){
				
				//caps maximum lines to send to the player at once - dS represents max allowed lines
				int line;
				int dS = 7;
				
				if((lineNumber * dS) < list.size() ){
					line = lineNumber * dS;
					
				}else { line = 0; lineNumber = 0; }
				
				Integer l = (Integer) (lineNumber + 1);
				
				sender.sendMessage((this.type == validMapType.PRIVATE) ? 
				(ChatColor.GOLD + "Artworks by " + ChatColor.AQUA + ((Player) sender).getName()) :
				(ChatColor.GOLD + "Public Artworks") ) ;
				
				sender.sendMessage(ChatColor.DARK_PURPLE + "--------" + ChatColor.LIGHT_PURPLE +
				"[pg " + lineNumber + "]" + ChatColor.DARK_PURPLE + "--------");
				
				for(int i = line; i < list.size() && i < (lineNumber + dS); i ++){
					
					AbstractMapArt art = list.get(i);
					Artwork a = (Artwork) art;
					if(a != null)
						
					sender.sendMessage(ChatColor.GOLD+ "•  " + ChatColor.AQUA+ a.getTitle() +
					ChatColor.GOLD + " by " + ChatColor.DARK_AQUA + Bukkit.getPlayer(a.getArtist()).getName() );
						
				}if(list.size() > (dS + line) ) sender.sendMessage(
						
					ChatColor.DARK_AQUA + "Type " + ChatColor.DARK_PURPLE + "/artmap list " + type.toString().toLowerCase() + 
					ChatColor.LIGHT_PURPLE + " [" + l + "] " + ChatColor.DARK_AQUA + "to see more");
				
				list = null;
				
			}else sender.sendMessage(String.format(Error.noArtwork, type.toString().toLowerCase()));
		}
	}
	
	private boolean sort(){
		
		if(keys.size() > 0){
			Iterator<String> compiler = keys.iterator();
			list = new ArrayList<>();
			
			while(compiler.hasNext()){
				
				AbstractMapArt m = StartClass.artList.get(compiler.next());
				
				if(m.getType() == type){
					
					if(m.getType() == validMapType.PRIVATE){
						
						if(Bukkit.getOfflinePlayer(m.getArtist()) == 
						(OfflinePlayer) sender ){
							
							list.add(m);
						}
					}else list.add(m);
				}
			}
			return true;
		}else sender.sendMessage(String.format(Error.noArtwork, type.toString().toLowerCase()));
	return false;
	}
	
	private static validMapType resolveType(String[] type){
		if(type.length < 2) return validMapType.PRIVATE;
		switch(type[1].toLowerCase()){
		case "private" : return validMapType.PRIVATE;
		case "public" : return validMapType.PUBLIC;
		case "template" : return validMapType.TEMPLATE;
		case "queued" : return validMapType.PRIVATE_QUEUED;
		default : return validMapType.PRIVATE;
		}
	}
}

