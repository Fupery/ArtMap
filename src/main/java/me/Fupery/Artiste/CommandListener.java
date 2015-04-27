package me.Fupery.Artiste;

import me.Fupery.Artiste.Command.Help;
import me.Fupery.Artiste.Command.CanvasCommands.*;
import me.Fupery.Artiste.Command.MapArtCommands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandListener implements CommandExecutor{
	
	private CommandSender sender;
	private String[] args;

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		this.sender = sender;
		this.args = args;
		
		if(args.length == 0) return false;
		
		switch (args[0].toLowerCase()){
		
		case "help"      : new Help(this).check(); break;
		
		case "info"      : new Info(this).check(); break;
		
		//Staff commands
		case "define"    : new Define(this).check(); break;
		
		case "remove"    : new Remove(this).check(); break;
		
		case "approve"   : new Info(this).check(); break;
		
		case "deny"      : new Info(this).check(); break;
		
		//Canvas commands
		case "claim"     : new Claim(this).check(); break;
		
		case "unclaim"   : new Unclaim(this).check(); break;
		
		case "addmember" : new AddMember(this).check(); break;
		
		case "reset"     : new Reset(this).check(); break;
		
		//MapArt commands
		case "save"      : new Save(this).check(); break;
		
		case "delete"    : new Delete(this).check(); break;
		
		case "edit"      : new Edit(this).check(); break;
		
		case "buy"       : new Buy(this).check(); break;
		
		case "publish"   : new Publish(this).check(); break;
		
		default : sender.sendMessage(ChatColor.RED + "/artmap help for more commands");
		}
		return true;
	}

	public CommandSender getSender() {
		return sender;
	}

	public String[] getArgs() {
		return args;
	}

	
}
