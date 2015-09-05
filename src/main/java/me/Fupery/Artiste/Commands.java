package me.Fupery.Artiste;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private Artiste plugin;

    public Commands(Artiste plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && args.length > 0) {

            if (args[0].equalsIgnoreCase("getMap")) {
                //currently redundant
            }
        }
        return true;
    }
}
