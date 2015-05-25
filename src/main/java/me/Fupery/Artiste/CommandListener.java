package me.Fupery.Artiste;

import me.Fupery.Artiste.Command.Ban;
import me.Fupery.Artiste.Command.Help;
import me.Fupery.Artiste.Command.List;
import me.Fupery.Artiste.Command.CanvasCommands.*;
import me.Fupery.Artiste.Command.MapArtCommands.*;
import me.Fupery.Artiste.Command.Utils.AbstractCommand;
import me.Fupery.Artiste.Command.Utils.Test;
import me.Fupery.Artiste.Command.Utils.Error;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandListener implements CommandExecutor {

	private CommandSender sender;
	private String[] args;

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		this.sender = sender;
		this.args = args;

		if (args.length == 0) {

			sender.sendMessage(Error.usage);
			return true;
		}
		
		switch (args[0].toLowerCase()) {
		
		case "help"      : add(new Help()); break;
		
		case "info"      : add(new Info()); break;
		
		//Staff commands
		case "define"    : add(new Define()); break;
		
		case "remove"    : add(new Remove()); break;
		
		case "approve"   : ;
		
		case "deny"      : add(new PublishEval()); break;
		
		case "ban"       : ;
		
		case "unban"     : add(new Ban()); break;
		
		//Canvas commands
		case "claim"     : add(new Claim()); break;
		
		case "unclaim"   : add(new Unclaim()); break;
		
		case "addmember" : ;
		
		case "delmember" : add(new AddMember()); break;
		
		case "reset"     : add(new Reset()); break;
		
		//MapArt commands
		case "save"      : add(new Save()); break;
		
		case "delete"    : add(new Delete()); break;
		
		case "edit"      : add(new Edit()); break;
		
		case "buy"       : add(new Buy()); break;
		
		case "publish"   : add(new Publish()); break;
		
		case "list"      : add(new List()); break;
		
		case "test"      : add(new Test()); break;
		
		default : sender.sendMessage(ChatColor.RED + "/artmap help for more commands");
		}

		return true;
	}

	public void add(AbstractCommand command) {

		command.pass(this.sender, this.args);
		
		command.initialize();

		command.check();

	}
}
