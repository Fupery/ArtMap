package me.Fupery.Artiste;

import java.util.UUID;

import me.Fupery.Artiste.Command.*;
import me.Fupery.Artiste.Command.Error;
import me.Fupery.Artiste.IO.Artist;
import me.Fupery.test.IDUpdate;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandList implements CommandExecutor{
	
	private CommandSender sender;
	public StartClass plugin;
	
	//TODO - implement publish system
	@Override
	public boolean onCommand(CommandSender sender, Command command,
	String label, String[] args) {
		
		if(command.getName().equalsIgnoreCase("artmap")){
			
			this.sender = sender;
			
			if(!isArtist() && (sender instanceof Player)){
				
				UUID id = ((Player) sender).getUniqueId();
				StartClass.artistList.put(id, new Artist(id));
			}
			
			Handler h = new Handler(sender, args);
			MapHandler mh = new MapHandler(sender, args);
			Canvas c = Canvas.findCanvas();
			
			if(args.length == 0){
				sender.sendMessage(Error.usage("") );
				return true;
			}
			switch(args[0].toLowerCase()){
			//                             min|max|canvas|claim |admin
			case "define"    : if(h.evalCmd(4, 4, false, false, true )) Define.define(sender, args); break;
			case "remove"    : if(h.evalCmd(1, 1, true,  false, true )) c.removeCanvas(sender); break;
			case "info"      : if(h.evalCmd(1, 1, true,  false, true )) c.getCanvasData(sender); break;
			case "approve"   : if(h.evalCmd(2, 2, true,  false, true )) ; break;
			
			case "claim"     : if(h.evalCmd(1, 1, true,  false, false)) c.claim(sender, plugin); break;
			case "addmember" : if(h.evalCmd(2, 2, true,  true,  false)) c.addMember(sender, args[1]); break;
			case "reset"     : if(h.evalCmd(1, 2, true,  true,  false)) Reset.reset(sender, args); break;
			case "unclaim"   : if(h.evalCmd(1, 1, true,  true,  false)) c.unclaim(sender, plugin); break;
			
			case "save"      : if(h.evalCmd(2, 2, true,  true,  false)) mh.save(sender, args); break;
			case "edit"      : if(h.evalCmd(2, 2, true,  true,  false)) mh.edit(); break;
			case "publish"   : if(h.evalCmd(2, 2, false, false, false)) mh.publish(); break;
			case "delete"    : if(h.evalCmd(2, 2, false, false, false)) mh.delete(); break;
			case "list"      : if(h.evalCmd(1, 3, false, false, false)) List.initialize(sender, args); break;
			case "buy"       : if(h.evalCmd(2, 2, false, false, false)) mh.buy(); break;
			case "test"      : new IDUpdate(sender); break;
						
			case "help"      : new Help(sender); break;
			default : sender.sendMessage(Error.usage(args[0]) );
			}
			return true;
		}
		return false;
	}
	private boolean isArtist() {
		
		if(sender instanceof Player){
			
			UUID id = ((Player) sender).getUniqueId();
			
			if(StartClass.artistList.get(id) != null)
				return true;
		}
	return false;
	}
	public CommandList(StartClass plugin) {
		this.plugin = plugin;
	}
}
