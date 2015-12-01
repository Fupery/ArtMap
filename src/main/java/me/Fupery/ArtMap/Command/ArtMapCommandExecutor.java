package me.Fupery.ArtMap.Command;

import me.Fupery.ArtMap.ArtMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

import static me.Fupery.ArtMap.Utils.Formatting.playerError;

public class ArtMapCommandExecutor implements CommandExecutor {

    private final ArtMap plugin;
    private HashMap<String, ArtMapCommand> commands;

    public ArtMapCommandExecutor(final ArtMap plugin) {
        this.plugin = plugin;
        commands = new HashMap<>();
        //Commands go here - note that they are run on an async thread

        commands.put("save", new CommandSave(plugin));

        commands.put("delete", new CommandDelete(plugin));

        commands.put("preview", new CommandPreview(plugin));

        commands.put("list", new CommandList(plugin));

        commands.put("help", new CommandHelp(plugin));

        commands.put("get", new CommandGet(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {

            if (commands.containsKey(args[0].toLowerCase())) {
                commands.get(args[0].toLowerCase()).runPlayerCommand(sender, args);

            } else {
                sender.sendMessage(playerError("/artmap help for a list of commands."));
            }

        } else {
            commands.get("help").runPlayerCommand(sender, args);
        }
        return true;
    }

    public ArtMap getPlugin() {
        return plugin;
    }
}