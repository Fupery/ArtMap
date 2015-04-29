package me.Fupery.Artiste;

import me.Fupery.Artiste.Command.AbstractCommand;
import me.Fupery.Artiste.Command.Help;
import me.Fupery.Artiste.Command.CanvasCommands.*;
import me.Fupery.Artiste.Command.MapArtCommands.*;
import me.Fupery.test.Test;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandListener implements CommandExecutor{
	
	private CommandSender sender;
	private String[] args;
	private AbstractCommand cmd;

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		this.sender = sender;
		this.args = args;
		
		if(args.length == 0){
			
			sender.sendMessage(ChatColor.RED + "/artmap help for more commands");
			return true;
		}
		
		switch (args[0].toLowerCase()){
		
		case "help"      : new Help(this).check(); break;
		
		case "info"      : new Info(this).check(); break;
		
		//Staff commands
		case "define"    : new Define(this).check(); break;
		
		case "remove"    : new Remove(this).check(); break;
		
		case "approve"   : new PublishEval(this).check(); break;
		
		case "deny"      : new PublishEval(this).check(); break;
		
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
		
		case "list"      : new List(this).check(); break;
		
		case "test" : new Test(this).check();
		
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

	public AbstractCommand getCmd() {
		return cmd;
	}

	public void setCmd(AbstractCommand cmd) {
		this.cmd = cmd;
	}

	
}
