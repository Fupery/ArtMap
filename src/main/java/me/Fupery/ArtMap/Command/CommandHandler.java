package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.InventoryMenu.HelpMenu.ArtworkMenu;
import me.Fupery.ArtMap.InventoryMenu.HelpMenu.HelpMenu;
import me.Fupery.ArtMap.Utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CommandHandler implements CommandExecutor {

    private final HashMap<String, Command> commands;

    public CommandHandler() {
        commands = new HashMap<>();
        //Commands go here - note that they are run on an async thread

        commands.put("save", new CommandSave());

        commands.put("delete", new CommandDelete());

        commands.put("preview", new CommandPreview());

        commands.put("backup", new CommandBackup());

        commands.put("restore", new CommandRestore());

        //convenience commands
        commands.put("help", new Command(null, "/artmap [help]", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

                if (sender instanceof Player) {
                    HelpMenu.helpMenu.open((Player) sender);

                } else {
                    sender.sendMessage(Lang.Array.CONSOLE_HELP.messages());
                }
            }
        });
        commands.put("recipe", new Command(null, "/artmap recipe", false) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                HelpMenu.helpMenu.getButton(1).onClick((Player) sender);
            }
        });
        commands.put("list", new Command(null, "/artmap list [player]", false) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

                if (args.length > 1) {

                    OfflinePlayer artist;
                    artist = Bukkit.getOfflinePlayer(args[1]);

                    if (artist == null || !artist.hasPlayedBefore()) {
                        msg.message = String.format(Lang.PLAYER_NOT_FOUND.message(), args[1]);
                        return;
                    }
                    UUID uuid = artist.getUniqueId();

                    ArtworkMenu menu = new ArtworkMenu(null, uuid);
                    menu.open((Player) sender);

                } else {

                    HelpMenu.helpMenu.getButton(4).onClick((Player) sender);
                }
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (args.length > 0) {

            if (commands.containsKey(args[0].toLowerCase())) {
                commands.get(args[0].toLowerCase()).runPlayerCommand(sender, args);

            } else {
                sender.sendMessage(Lang.HELP.message());
            }

        } else {
            commands.get("help").runPlayerCommand(sender, args);
        }
        return true;
    }

}