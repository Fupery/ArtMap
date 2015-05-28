package me.Fupery.Artiste;

import me.Fupery.Artiste.Command.Ban;
import me.Fupery.Artiste.Command.Help;
import me.Fupery.Artiste.Command.List;
import me.Fupery.Artiste.Command.CanvasCommands.*;
import me.Fupery.Artiste.Command.MapArtCommands.*;
import me.Fupery.Artiste.Command.Utils.AbstractCommand;
import me.Fupery.Artiste.Command.Utils.CommandType;
import me.Fupery.Artiste.Command.Utils.Error;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandListener implements CommandExecutor {

	private CommandSender sender;
	private String[] args;
	private CommandType type;

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		this.sender = sender;
		this.args = args;

		if (args.length == 0) {
			sender.sendMessage(Error.usage);
			return true;
		}
		this.type = CommandType.getType(args[0]);

		switch (type) {
		
		case HELP      : add(new Help()); break;
		
		case INFO      : add(new Info()); break;
		
		//Staff commands
		case DEFINE    : add(new Define()); break;
		
		case REMOVE    : add(new Remove()); break;
		
		case APPROVE   : ;
		
		case DENY      : add(new PublishEval()); break;
		
		case BAN       : ;
		
		case UNBAN     : add(new Ban()); break;
		
		//Canvas commands
		case CLAIM     : add(new Claim()); break;
		
		case UNCLAIM   : add(new Unclaim()); break;
		
		case ADDMEMBER : ;
		
		case DELMEMBER : add(new AddMember()); break;
		
		case RESET     : add(new Reset()); break;
		
		//MapArt commands
		case SAVE      : add(new Save()); break;
		
		case DELETE    : add(new Delete()); break;
		
		case EDIT      : add(new Edit()); break;
		
		case BUY       : add(new Buy()); break;
		
		case PUBLISH   : add(new Publish()); break;
		
		case LIST      : add(new List()); break;
		
		default : sender.sendMessage(ChatColor.RED + "/artmap help for more commands");
		}
		return true;
	}

	public void add(AbstractCommand command) {
		command.pass(sender, args, type);
		command.initialize();
		command.check();
	}
}